package com.example.aula1;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunos extends AppCompatActivity {
    private ListView listView;
    private AlunoDAO alunoDAO;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        listView = findViewById(R.id.lista_alunos);
        this.alunoDAO = new AlunoDAO(this);
        this.alunos = this.alunoDAO.obterTodos();
        alunosFiltrados.addAll(alunos);

        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunos);
        listView.setAdapter(adaptador);
        registerForContextMenu(listView);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }

    public void voltar(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void excluir(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o aluno?")
                .setNegativeButton("NÃO",null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alunosFiltrados.remove(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        alunoDAO.excluir(alunoExcluir);
                        listView.invalidateViews();
                    }
                } ).create();
        dialog.show();
    }

    public void atualizar(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("aluno",alunoAtualizar);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alunos = alunoDAO.obterTodos();
        alunosFiltrados.clear();
        alunosFiltrados.addAll(alunos);
        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, alunosFiltrados);
        listView.setAdapter(adaptador);
    }

}