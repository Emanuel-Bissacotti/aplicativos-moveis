package com.example.aula1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class activity_endereco extends AppCompatActivity {

    private EditText editTextCep;
    private Button buttonBuscar;
    private EditText editTextLogradouro;
    private EditText editTextComplemento;
    private EditText editTextBairro;
    private EditText editTextCidade;
    private EditText editTextEstado;

    private Button buttoSalvarEndereco;

    private ViaCepService viaCepService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        editTextCep = findViewById(R.id.editTextCep);
        buttonBuscar = findViewById(R.id.buttonBuscar);
        editTextLogradouro = findViewById(R.id.editTextLogradouro);
        editTextComplemento = findViewById(R.id.editTextComplemento);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        viaCepService = retrofit.create(ViaCepService.class);

        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = editTextCep.getText().toString();
                if (cep.length() == 8) {
                    buscarEndereco(cep);
                } else {
                    Toast.makeText(activity_endereco.this, "CEP inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void buscarEndereco(String cep) {
        Call<Endereco> call = viaCepService.getEndereco(cep);
        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Endereco endereco = response.body();
                    preencherCampos(endereco);
                } else {
                    Toast.makeText(activity_endereco.this, "Erro ao buscar endereço", Toast.LENGTH_SHORT).show();
                    Log.e("EnderecoActivity", "Erro na requisição: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Toast.makeText(activity_endereco.this, "Erro na requisição", Toast.LENGTH_SHORT).show();
                Log.e("EnderecoActivity", "Erro na requisição", t);
            }
        });
    }

    private void preencherCampos(Endereco endereco) {
        if (endereco != null) {
            editTextLogradouro.setText(endereco.getLogradouro());
            editTextComplemento.setText(endereco.getComplemento());
            editTextBairro.setText(endereco.getBairro());
            editTextCidade.setText(endereco.getLocalidade());
            editTextEstado.setText(endereco.getUf());
        }
    }

    public void salvarEndereco(View view) {
        String cep = editTextCep.getText().toString();
        String logradouro = editTextLogradouro.getText().toString();
        String complemento = editTextComplemento.getText().toString();
        String bairro = editTextBairro.getText().toString();
        String cidade = editTextCidade.getText().toString();
        String estado = editTextEstado.getText().toString();

        StringBuilder enderecoCompleto = new StringBuilder();
        enderecoCompleto.append(logradouro);
        if (!complemento.isEmpty()) {
            enderecoCompleto.append(", ").append(complemento);
        }
        enderecoCompleto.append("\n").append(bairro);
        enderecoCompleto.append("\n").append(cidade).append(" - ").append(estado);
        enderecoCompleto.append("\nCEP: ").append(cep);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("enderecoCompleto", enderecoCompleto.toString());
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Endereço salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void voltar(View view) {
        finish();
    }
}