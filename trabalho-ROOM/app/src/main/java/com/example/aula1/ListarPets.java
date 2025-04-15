package com.example.aula1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListarPets extends AppCompatActivity {
    private ListView listView;
    private PetRepository petRepo;
    private List<Pet> pets;
    private List<Pet> petsFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_pets);

        listView = findViewById(R.id.lista_alunos);
        petRepo = new PetRepository(this);
        pets = petRepo.obterTodos();
        petsFiltrados.addAll(pets);

        ArrayAdapter<Pet> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pets);
        listView.setAdapter(adaptador);
        registerForContextMenu(listView);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        // Chama o método da superclasse (neste caso, o método onCreateContextMenu da classe pai).
        // Isso é importante para garantir que qualquer comportamento padrão do método na superclasse
        // (por exemplo, qualquer configuração padrão de menu que a superclasse realiza) seja executado antes
        // de você adicionar suas próprias ações ao menu.
        super.onCreateContextMenu(menu, v, menuInfo);

        // Cria um objeto MenuInflater, que é responsável por inflar (converter um arquivo XML de menu em um objeto Menu)
        // o menu de contexto a partir de um arquivo XML de menu que você criou anteriormente.
        MenuInflater i = getMenuInflater();

        // O método inflate do MenuInflater é usado para inflar o menu de contexto.
        // Aqui, você está especificando o recurso XML (R.menu.menu_contexto) que define as opções de menu
        // que aparecerão quando um item da lista for pressionado.
        i.inflate(R.menu.menu_contexto, menu); //Aqui coloca o nome do menu que havia sido configurado
    }

    public void voltar(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void excluir(MenuItem item){
        //pegar qual a posicao do item da lista que eu selecionei para excluir
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Pet petExcluir = petsFiltrados.get(menuInfo.position);
        //mensagem perguntando se quer realmente excluir
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o pet?")
                .setNegativeButton("NÃO",null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        petRepo.excluir(petExcluir);
                        petRepo.excluir(petExcluir);
                        pets.remove(petExcluir);
                        petRepo.excluir(petExcluir);
                        listView.invalidateViews();
                    }
                } ).create(); //criar a janela
        dialog.show(); //manda mostrar a janela
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega todos os alunos do banco de dados
        pets = petRepo.obterTodos();
        // Limpa a lista filtrada e adiciona os novos alunos
        petsFiltrados.clear();
        petsFiltrados.addAll(pets);
        // Atualiza o adapter da ListView para refletir os novos dados
        ArrayAdapter<Pet> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, petsFiltrados);
        listView.setAdapter(adaptador);
    }
}