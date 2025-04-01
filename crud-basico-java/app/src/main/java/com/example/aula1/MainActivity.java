package com.example.aula1;

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
import android.Manifest;

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

    private AlunoDAO dao;

    private Aluno aluno = null;
    private ImageView imageView;
    private static  final int REQUEST_CAMERA_PERMISSION = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private TextView txtEndereco;
    private static final int REQUEST_ENDERECO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editTextText);
        cpf = findViewById(R.id.editTextText2);
        telefone = findViewById(R.id.editTextText3);
        imageView = findViewById(R.id.imageView);

        Button btnTakePhoto = findViewById(R.id.button4);
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Obter os dados da intenção de retorno
                    Intent data = result.getData();
                    // Obter os extras da intenção (que contêm a imagem capturada)
                    Bundle extras = data.getExtras();
                    // Obter a imagem capturada como um objeto Bitmap
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    // Corrige a orientação antes de exibir – Giro de 90 graus
                    Bitmap imagemCorrigida = this.corrigirOrientacao(imageBitmap);
                    // Exibir a imagem na ImageView
                    imageView.setImageBitmap(imagemCorrigida);
                }
            }
        );

        dao = new AlunoDAO(this);

        txtEndereco = findViewById(R.id.txtEndereco);

        Intent it = getIntent();
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCPF());
            telefone.setText(aluno.getTelefone());

            byte[] fotoBytes = aluno.getFotoBytes();
            if (fotoBytes != null && fotoBytes.length > 0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap corrigirOrientacao(Bitmap bitmap) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    public void salvar(View view){
        if(aluno==null) {
            Aluno a = new Aluno(
                    nome.getText().toString(),
                    cpf.getText().toString(),
                    telefone.getText().toString()
            );

            if (a.getNome().isEmpty() || a.getCPF().isEmpty() || a.getTelefone().isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dao.isCPF(a.getCPF())) {
                Toast.makeText(this, "CPF Invalido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dao.cpfDuplicado(a.getCPF())) {
                Toast.makeText(this, "CPF ja cadastrado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageView.getDrawable() != null) {
                BitmapDrawable drawable = (BitmapDrawable)
                        imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = stream.toByteArray();
                System.out.println(fotoBytes);
                a.setFotoBytes(fotoBytes);
            }

            long id = dao.inserir(a);
            Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
        } else{
            aluno.setNome(nome.getText().toString());
            aluno.setCPF(cpf.getText().toString());
            aluno.setTelefone(telefone.getText().toString());
            if (imageView.getDrawable() != null) {
                BitmapDrawable drawable = (BitmapDrawable)
                        imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = stream.toByteArray();
                aluno.setFotoBytes(fotoBytes);
            }
            dao.atualizar(aluno);
            Toast.makeText(this,"Aluno Atualizado!! com id: ", Toast.LENGTH_SHORT).show();
        }
    }

    public void irParaListar(View view){
        Intent intent = new Intent(this, ListarAlunos.class);
        startActivity(intent);
    }

    public void irParaEndereco(View view){
        Log.d("MainActivity", "irParaEndereco method called");
        Intent intent = new Intent(MainActivity.this, ActivityEndereco.class);
        startActivityForResult(intent, REQUEST_ENDERECO);
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

    public void tirarFoto(View view) {
        this.checkCameraPermissionAndStart();

    }

    private void checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            System.out.println("aqui");
        } else {
            this.startCamera();
        }
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println(takePictureIntent.resolveActivity(getPackageManager()));

        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            System.out.println("camera");
            cameraLauncher.launch(takePictureIntent);
        }
    }
}