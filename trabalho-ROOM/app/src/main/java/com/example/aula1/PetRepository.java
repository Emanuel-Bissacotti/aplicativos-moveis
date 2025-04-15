package com.example.aula1;

import android.content.Context;
import androidx.room.Room;

import java.util.List;

public class PetRepository {

    private PetDAO petDAO;

    public PetRepository(Context context) {
        // Inicia o banco de dados Room
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "pet-database")
                .allowMainThreadQueries()  // Importante: evitar chamadas de banco de dados no main thread para apps reais
                .build();
        petDAO = db.petDAO();
    }

    public long inserir(Pet pet) {
        return petDAO.inserir(pet);
    }

    public List<Pet> obterTodos() {
        return petDAO.obterTodos();
    }

    public Pet obterPorCpf(String cpf) {
        return petDAO.obterPorCpf(cpf);
    }

    public void excluir(Pet pet) {
        petDAO.excluir(pet);
    }

    public void atualizar(Pet pet) {
        petDAO.atualizar(pet);
    }

    public boolean cpfDuplicado(String cpf) {
        Pet pet = petDAO.obterPorCpf(cpf);
        return pet != null;
    }
}
