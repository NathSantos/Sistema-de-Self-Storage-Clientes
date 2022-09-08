package br.edu.icomp.vaultstorage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "VaultStorage.db";
    private static final String SQL_CREATE_CLIENTES = "CREATE TABLE clientes (" +
            "nome TEXT(20) NOT NULL, cpf TEXT(11) PRIMARY KEY, senha TEXT(6) NOT NULL)";
    private static final String SQL_CREATE_LOCDISP = "CREATE TABLE locaisDisponiveis (" +
            "numero INTEGER PRIMARY KEY, categoria TEXT(14) NOT NULL)";
    private static final String SQL_CREATE_LOCALUGADOS = "CREATE TABLE locaisAlugados (" +
            "numero INTEGER PRIMARY KEY, categoria TEXT(14) NOT NULL, cpf TEXT(11) NOT NULL," +
            "dataInicio DATE NOT NULL, dataTermino DATE NOT NULL, custoPorMes INTEGER NOT NULL," +
            "meses INTEGER NOT NULL, custoLocal INTEGER NOT NULL, custoOpcionais INTEGER, custoTotal INTEGER NOT NULL," +
            "FOREIGN KEY(numero) REFERENCES locaisDisponiveis(numero))";
    private static final String SQL_DELETE_CLIENTES = "DROP TABLE IF EXISTS clientes";
    private static final String SQL_DELETE_LOCDISP = "DROP TABLE IF EXISTS locaisDisponiveis";
    private static final String SQL_DELETE_LOCALUGADOS = "DROP TABLE IF EXISTS locaisAlugados";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CLIENTES);
        db.execSQL(SQL_CREATE_LOCDISP);
        db.execSQL(SQL_CREATE_LOCALUGADOS);

        // PREENCHENDO TABELA DE LOCAIS DISPONÍVEIS
        for(int i=1; i<=100; i++) {
            Local local = new Local(i);
            String categoria = local.getCategoria().getCategoria();
            String comando = "INSERT INTO locaisDisponiveis VALUES " +
                    "(" + i + ", '" + categoria + "')";
            db.execSQL(comando);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CLIENTES);
        db.execSQL(SQL_DELETE_LOCDISP);
        db.execSQL(SQL_DELETE_LOCALUGADOS);
        onCreate(db);
    }

}
