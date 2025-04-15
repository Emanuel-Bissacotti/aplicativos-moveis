package com.example.aula1;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private EditText nome;
    private EditText cpf;
    private EditText telefone;

    private PetRepository dao;

    private Pet pet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editTextText);
        cpf = findViewById(R.id.editTextText2);
        telefone = findViewById(R.id.editTextText3);

        dao = new PetRepository(this);

        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("pet")){
            pet = (Pet) it.getSerializableExtra("pet");
            nome.setText(pet.getNome().toString());
            cpf.setText(pet.getCpf());
            telefone.setText(pet.getTelefone());
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

        if (pet == null) {
            Pet pet = new Pet();
            pet.setNome(nomeDigitado);
            pet.setCpf(cpfDigitado);
            pet.setTelefone(telefoneDigitado);

            System.out.println(dao.cpfDuplicado(pet.cpf));
            if (dao.cpfDuplicado(pet.cpf)){
                Toast.makeText(
                        this,
                        "Erro ao inserir pet CPF Duplicado.: " + pet.getCpf(), Toast.LENGTH_SHORT).show();
                return;
            }

            Long save = dao.inserir(pet);
            if (save != -1) {
                Toast.makeText(this, "Pet "+ pet.getNome() +" inserido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao inserir pet. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            pet.setNome(nomeDigitado);
            pet.setCpf(cpfDigitado);
            pet.setTelefone(telefoneDigitado);
            dao.atualizar(pet);
            Toast.makeText(this, "Pet atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    public void irParaListar(View view){
        Intent intent = new Intent(this, ListarPets.class);
        startActivity(intent);
    }

}