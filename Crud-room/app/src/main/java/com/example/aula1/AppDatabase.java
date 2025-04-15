package com.example.aula1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Define a classe como um banco de dados Room
// "entities" especifica as tabelas que pertencem ao banco
// "version" é usada para controle de atualização do banco de dados
@Database(entities = {Aluno.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // Método abstrato que retorna o DAO (Data Access Object) para acessar os dados da tabela "aluno"
    public abstract AlunoDao alunoDao();
    // Instância única do banco de dados (Singleton) para evitar múltiplas conexões
    private static AppDatabase INSTANCE;
    // Método para obter a instância do banco de dados
    // "synchronized" garante que apenas uma instância seja criada, mesmo em ambientes com múltiplas threads
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) { // Se o banco de dados ainda não foi criado, cria uma nova instância
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(), // Usa o contexto da aplicação para evitar vazamento de memória
                            AppDatabase.class, // Define esta classe como o banco de dados Room
                            "banco-de-dados" // Nome do arquivo do banco de dados armazenado no dispositivo
                    ).allowMainThreadQueries() // Permite rodar consultas no thread principal (não recomendado em produção)
                    .build(); // Cria e retorna a instância do banco de dados
        }
        return INSTANCE; // Retorna a instância única do banco de dados
    }
}