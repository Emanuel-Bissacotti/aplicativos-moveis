package com.example.aula1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

@Dao
public interface PetDAO {

    @Insert
    long inserir(Pet pet);

    @Query("SELECT * FROM pet")
    List<Pet> obterTodos();

    @Query("SELECT * FROM pet WHERE cpf = :cpf LIMIT 1")
    Pet obterPorCpf(String cpf);

    @Update
    void atualizar(Pet pet);

    @Delete
    void excluir(Pet pet);
}
