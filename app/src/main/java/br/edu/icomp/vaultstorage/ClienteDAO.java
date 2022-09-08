package br.edu.icomp.vaultstorage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

public class ClienteDAO {
    private Context context;
    private SQLiteDatabase database;

    /**
     * Construtor da classe
     */
    public ClienteDAO (Context context) {
        this.context = context;
        this.database = (new Database(context)).getWritableDatabase();
    }

    /**
     * Faz a validação de senha na hora de fazer o login
     */
    public boolean validaSenha(String cpf, String senha) {
        String sql = "SELECT cpf, senha FROM clientes WHERE cpf='" + cpf + "' and senha='" + senha + "'";
        Cursor cursor = database.rawQuery(sql, null);

        // Retorna se o cursor tem 1 resultado com o cpf e senha informados
        return cursor != null && cursor.getCount() == 1;
    }

    /**
     * Adiciona um cliente no BD
     */
    public boolean addCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes VALUES (" + "'" + cliente.getNome() + "', "
                + "'" + cliente.getCpf() + "', "
                + "'" + cliente.getSenha() + "')";
        try {
            database.execSQL(sql);
            Toast.makeText(context, "Cadastro Realizado!", Toast.LENGTH_SHORT).show();
            return true;
        }
        catch (SQLException e) {
            Toast.makeText(context, "Este CPF já está cadastrado!! ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * remove um cliente do BD
     */
    public boolean removerCliente(String cpf) {
        String sql = "DELETE FROM clientes WHERE cpf='" + cpf + "'";
        try {
            database.execSQL(sql);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    /**
     * Pega um cliente do BD de acordo com o cpf
     * @param cpf - cpf do cliente
     */
    public Cliente getCliente(String cpf) {
        String sql = "SELECT * FROM clientes WHERE cpf='" + cpf + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            String nome = cursor.getString(0);
            String cpF = cursor.getString(1);
            String senha = cursor.getString(2);
            return new Cliente(nome, cpF, senha);
        }
        return null;
    }

}
