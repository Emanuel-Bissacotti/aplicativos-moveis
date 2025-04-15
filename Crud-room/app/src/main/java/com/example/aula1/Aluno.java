package com.example.aula1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "aluno")
public class Aluno implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "nome")
    String nome;

    @ColumnInfo(name = "cpf")
    String CPF;

    @ColumnInfo(name = "telefone")
    String telefone;

    @ColumnInfo(name = "foto")
    private byte[] fotoBytes;

    public Aluno(String nome, String CPF, String telefone) {
        this.nome = nome;
        this.CPF = CPF;
        this.telefone = telefone;
    }

    public Aluno() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString(){
        return this.nome;
    }

    public byte[] getFotoBytes() {
        return fotoBytes;
    }




    public void setFotoBytes(byte[] fotoBytes) { this.fotoBytes = fotoBytes; }
}
