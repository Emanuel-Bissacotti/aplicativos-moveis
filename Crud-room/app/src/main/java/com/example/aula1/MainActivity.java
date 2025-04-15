package com.example.aula1;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private EditText nome;
    private EditText cpf;
    private EditText telefone;

    private AlunoValidator validator;

    private Aluno aluno = null;
    private ImageView imageView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private TextView txtEndereco;

    private AlunoDao alunoDao;
    private static final int REQUEST_ENDERECO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        Bitmap imagemCorrigida = corrigirOrientacao(imageBitmap);
                        imageView.setImageBitmap(imagemCorrigida);
                    }
                }

        );
        AppDatabase db = AppDatabase.getInstance(this);
        this.alunoDao = db.alunoDao();
        this.validator = new AlunoValidator(alunoDao);



        imageView = findViewById(R.id.imageView);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);


        nome = findViewById(R.id.editTextText);
        cpf = findViewById(R.id.editTextText2);
        telefone = findViewById(R.id.editTextText3);


        txtEndereco = findViewById(R.id.txtEndereco);

        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCPF());
            telefone.setText(aluno.getTelefone());
            byte[] fotoBytes = aluno.getFotoBytes();
            if (fotoBytes != null && fotoBytes.length>0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void salvar(View view){
        String nomeDigitado = nome.getText().toString().trim();
        String cpfDigitado = cpf.getText().toString().trim();
        String telefoneDigitado = telefone.getText().toString().trim();
        if (nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("CPF antes da validação: " + cpfDigitado);
        if (!validator.isCPF(cpfDigitado)) {
            Toast.makeText(this, "CPF inválido. Digite novamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(aluno == null || !cpfDigitado.equals(aluno.getCPF())){
            if (validator.cpfDuplicado(cpfDigitado)) {
                Toast.makeText(this, "CPF duplicado. Insira um CPF diferente.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        System.out.println("cu1: " + cpfDigitado);


        if (aluno == null) {
            Aluno aluno = new Aluno();
            aluno.setNome(nomeDigitado);
            aluno.setCPF(cpfDigitado);
            aluno.setTelefone(telefoneDigitado);
            if (imageView.getDrawable() != null) {
                BitmapDrawable drawable = (BitmapDrawable)
                        imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = stream.toByteArray();
                aluno.setFotoBytes(fotoBytes);
            }
            System.out.println("cu2: " + cpfDigitado);

            System.out.println(aluno.nome);
            long id = alunoDao.inserir(aluno);

            System.out.println("cu3: " + cpfDigitado);


            if (id != -1) {
                Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao inserir aluno. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            aluno.setNome(nomeDigitado);
            aluno.setCPF(cpfDigitado);
            aluno.setTelefone(telefoneDigitado);
            if (imageView.getDrawable() != null) {
                BitmapDrawable drawable = (BitmapDrawable)
                        imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = stream.toByteArray();
                aluno.setFotoBytes(fotoBytes);
            }

            alunoDao.atualizar(aluno);
            Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }

        finish();

    }


    public void irParaListar(View view){
        Intent intent = new Intent(this, ListarAlunos.class);
        startActivity(intent);
    }

    public void irParaEndereco(View view){
        Log.d("MainActivity", "irParaEndereco method called");
        Intent intent = new Intent(MainActivity.this, activity_endereco.class);
        startActivityForResult(intent, REQUEST_ENDERECO);
    }



    public void tirarFoto(View view){
        checkCameraPermissionAndStart();
    }
    private void checkCameraPermissionAndStart() {
        // Verifica se a permissão para usar a câmera já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, solicite-a
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Se a permissão já foi concedida, inicie a câmera
            startCamera();
        }
    }

    private void startCamera() {
        // Cria uma nova intenção para capturar uma imagem usando a ação padrão da câmera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verifica se existe uma atividade disponível no dispositivo que possa lidar com a intenção de captura de imagem
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            // Inicia a atividade de captura de imagem e espera o resultado através do 'cameraLauncher'
            cameraLauncher.launch(takePictureIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap corrigirOrientacao(Bitmap bitmap) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // Rotaciona a imagem em 90 graus (padrão para fotos invertidas)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENDERECO && resultCode == RESULT_OK && data != null) {
            String enderecoCompleto = data.getStringExtra("enderecoCompleto");
            if (enderecoCompleto != null && !enderecoCompleto.isEmpty()) {
                txtEndereco.setText(enderecoCompleto);
            }
        }
    }

}