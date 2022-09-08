package br.edu.icomp.vaultstorage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Classe LocaisDAO - Classe intermediária entre as tabelas locaisTotais, locaisDisponiveis, locaisAlugados (mysql) e as classes Cliente, Categoria, Local, infoAluguel, Opcional (java)
 * !!! É NECESSÁRIO EXECUTAR ESSA CLASSE PELA PRIMEIRA APÓS TER CRIADO O BANCO DE DADOS PARA PREENCHER A TABELA LOCAIS TOTAIS E LOCAIS DISPONÍVEIS !!!
 * @author Nathália Santos &lt;nathsantos2703@gmail.com&gt;
 * @version 1.0, 2022-08-06
 */
public class LocaisDAO extends AppCompatActivity {
    private Context context;
    private SQLiteDatabase database;

    public LocaisDAO (Context context) {
        this.context = context;
        this.database = (new Database(context)).getWritableDatabase();
    }

    // -------------------------- TABELA LOCAIS DISPONÍVEIS ------------------------------------
    /**
     * Adiciona um local na tabela locaisDisponiveis
     * @param numero - numero do local
     * @param categoria - categoria do local
     * @return boolean
     */
    public boolean adicionarDisponivel(String numero, String categoria) {
        String sql = "INSERT INTO locaisDisponiveis VALUES ('" + numero + "', '" + categoria + "')";
        try {
            database.execSQL(sql);
            System.out.println("Local disponível adicionado!");
            return true;
        }
        catch (SQLException e) {
            System.out.println("Erro ao adicionar local disponível!");
            return false;
        }
    }

    /**
     * Remove um local da tabela locaisDisponiveis
     * @param numero - numero do local
     * @return boolean
     */
    public boolean removerLocalDisponivel(String numero) {
        String sql = "DELETE FROM locaisDisponiveis WHERE numero='" + numero + "'";
        try {
            database.execSQL(sql);
            System.out.println("Local disponível removido!");
            return true;
        }
        catch (SQLException e) {
            System.out.println("Erro ao remover local disponível!");
            return false;
        }
    }

    // ----------------------------- TABELA LOCAIS ALUGADOS ------------------------------------

    /**
     * Pega a quantidade de locais alugados por cpf
     * @return int quantidade de locais alugados por cpf
     */
    public int quantAlugadosPorCpf(String cpf) {
        String sql = "SELECT * FROM locaisAlugados WHERE cpf='" + cpf + "'";
        Cursor cursor = database.rawQuery(sql, null);
        int quantAlugados = 0;

        while (cursor.moveToNext()) {
            quantAlugados++;
        }
        return quantAlugados;
    }

    /**
     * Adiciona um local na tabela locaisAlugados
     * @param numero - numero do local
     * @param categoria - categoria do local
     * @param cpf - cpf do cliente que alugou o local
     * @param inicio - data de início do aluguel
     * @param termino - data de término do aluguel
     * @param custoPorMes - custo por mês do local
     * @param meses - quantidade de meses de aluguel
     * @param custoLoc - custo total do local (custoPorMes x meses)
     * @param custoOpc - custo total dos opcionais selecionados
     * @param custoTotal - custo total do aluguel (custoLoc + custoOpc)
     * @return boolean
     */
    public boolean adicionarAlugado(String numero, String categoria, String cpf, String inicio, String termino, int custoPorMes, int meses, int custoLoc, int custoOpc, int custoTotal) {
        String sql = "INSERT INTO locaisAlugados VALUES ('" + numero + "', '" + categoria + "', '" +
                cpf + "', '" + inicio + "', '" + termino + "', " + custoPorMes + ", " + meses + ", " +
                custoLoc + ", " + custoOpc + ", " + custoTotal + ")";
        try {
            database.execSQL(sql);
            System.out.println("Local alugado adicionado!");
            return true;
        }
        catch (SQLException e) {
            System.out.println("Erro ao adicionar local alugado!");
            return false;
        }
    }

    /**
     * Procura disponibilidade para a categoria do local escolhido
     * Caso tenha disponibilidade, o local é alugado
     * @param cpf - cpf do cliente que alugou o local
     * @param categor - categoria do local
     * @param inicio - data de início do aluguel
     * @param termino - data de término do aluguel
     * @param seguro - (1 ou 0) se o cliente escolheu o seguro ou não
     * @param chave - (1 ou 0) se o cliente escolheu a chave extra ou não
     * @param controle - (1 ou 0) se o cliente escolheu o controle climático ou não
     * @return boolean
     */
    public boolean procuraDisponibilidadeEAluga(String cpf, String categor, String inicio, String termino, boolean seguro, boolean chave, boolean controle) throws ParseException {
        String sql = "SELECT * FROM locaisDisponiveis WHERE categoria='" + categor + "' ORDER BY numero ASC limit 1";
        Cursor cursor = database.rawQuery(sql, null);
        boolean achouDisponibilidade = false;

        Opcionais opc = new Opcionais(seguro, chave, controle);
        Categoria cat = new Categoria(categor);
        int custoPorMes = cat.getCusto();
        int meses = calculaMeses(inicio, termino);
        int custoLocal = custoPorMes * meses;
        int custoTotal = custoLocal + opc.getCustoOpcionais();

        while (cursor.moveToNext()) {
            // adicionando nos Locais Alugados
            adicionarAlugado(cursor.getString(0), cursor.getString(1), cpf, inicio, termino, custoPorMes, meses, custoLocal, opc.getCustoOpcionais(), custoTotal);
            // removendo dos Locais Disponíveis
            removerLocalDisponivel(cursor.getString(0));
            achouDisponibilidade = true;
        }

        if(achouDisponibilidade == true) {return true;}
        else {return false;}
    }

    /**
     * Calcula a quantidade de meses de aluguel
     * O custo mínimo de aluguel é cobrado por 1 mês, mesmo que o período de aluguel seja menor que 1 mês
     * @param inicio - data de início do aluguel
     * @param termino - data de término do aluguel
     * @return int meses
     */
    public int calculaMeses(String inicio, String termino) throws ParseException { // "yyyy-MM-dd"

        // String -> Date
        Date dataInicio = new SimpleDateFormat("yyyy-MM-dd").parse(inicio);
        Date dataTermino = new SimpleDateFormat("yyyy-MM-dd").parse(termino);

        // Date -> Calendar (define datas)
        Calendar dateIni = Calendar.getInstance();
        dateIni.setTime(dataInicio);
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(dataTermino);

        // Calcula diferença
        int meses = (dateEnd.get(Calendar.YEAR) * 12 + dateEnd.get(Calendar.MONTH))
                - (dateIni.get(Calendar.YEAR) * 12 + dateIni.get(Calendar.MONTH));

        // O menor valor possível será o de 1 mês de aluguel, mesmo que a diferença de datas seja menor que 30 dias
        if (meses == 0) {
            meses = 1;
        }

        return meses;
    }

    /**
     * Cancela o aluguel do local de acordo com o número do local e cpf do cliente que fez o aluguel
     * O local é removido da tabela locaisAlugados e adicionado na tabela locaisDisponiveis
     * @param cpf - cpf do cliente que alugou o local
     * @param numero - numero do local
     * @return boolean
     */
    public boolean cancelaAluguel(String cpf, String numero) {
        String sql = "SELECT * FROM locaisAlugados WHERE numero='" + numero + "' AND cpf='" + cpf + "' ORDER BY numero ASC limit 1";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.moveToNext()) {
            // removendo dos Locais Alugados
            removerLocalAlugado(cursor.getString(0));
            // adicionando nos Locais Disponíveis
            adicionarDisponivel(cursor.getString(0), cursor.getString(1));
            return true;
        }
        return false;
    }

    /**
     * Cancela todos os aluguéis de um determinado cliente (cpf)
     * O local é removido da tabela locaisAlugados e adicionado na tabela locaisDisponiveis
     * @param cpf - cpf do cliente que alugou o local
     * @return boolean
     */
    public boolean cancelaTodosAlugueisPorCpf(String cpf) {
        String sql = "SELECT * FROM locaisAlugados WHERE cpf='" + cpf + "'";
        Cursor cursor = database.rawQuery(sql, null);
        boolean flag = false;

        while(cursor.moveToNext()) {
            // removendo dos Locais Alugados
            removerLocalAlugado(cursor.getString(0));
            // adicionando nos Locais Disponíveis
            adicionarDisponivel(cursor.getString(0), cursor.getString(1));
            flag = true;
        }

        if(flag) {return true;}
        else{return false;}
    }

    /**
     * Altera as datas de início e término de um aluguel
     * @param cpf - cpf do cliente que alugou o local
     * @param numero - número do local
     * @param inicio - data de início do aluguel
     * @param termino - data de término do aluguel
     * @return boolean
     */
    public boolean alteraDataAluguel(String cpf, String numero, String inicio, String termino) throws ParseException {
        String sql = "SELECT custoPorMes, custoOpcionais FROM locaisAlugados WHERE numero='" + numero + "'";
        Cursor cursor = database.rawQuery(sql, null);

        int meses = calculaMeses(inicio, termino);
        int custoPorMes = 0;
        int custoOpcionais = 0;

        if(cursor.moveToNext()) {
            custoPorMes = cursor.getInt(0);
            custoOpcionais = cursor.getInt(1);
        }

        int custoLocal = custoPorMes * meses;
        int custoTotal = custoOpcionais + custoLocal;

        String sqlUpdate = "UPDATE locaisAlugados SET dataInicio='" + inicio + "', dataTermino='" + termino +
                    "', meses='" + meses + "', custoLocal='" + custoLocal + "', custoTotal='" + custoTotal +
                    "' WHERE numero='" + numero + "' AND cpf='" + cpf + "'";

        try {
            database.execSQL(sqlUpdate);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    /**
     * Pega a data de início do aluguel
     * @param numero - número do local
     * @return String
     */
    public String getDataInicio(String numero) {
        String sql = "SELECT dataInicio FROM locaisAlugados WHERE numero='" + numero + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            String dataInicio = cursor.getString(0);
            return dataInicio;
        }
        return null;
    }

    /**
     * Pega a data de término do aluguel
     * @param numero - número do local
     * @return String
     */
    public String getDataTermino(String numero) {
        String sql = "SELECT dataTermino FROM locaisAlugados WHERE numero='" + numero + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            String dataTermino = cursor.getString(0);
            return dataTermino;
        }
        return null;
    }

    /**
     * Remove um local da tabela locaisAlugados
     * @param numero - número do local
     * @return boolean
     */
    public boolean removerLocalAlugado(String numero) {
        String sql = "DELETE FROM locaisAlugados WHERE numero='" + numero + "'";
        try {
            database.execSQL(sql);
            System.out.println("Local alugado removido!");
            return true;
        }
        catch (SQLException e) {
            System.out.println("Erro ao remover local alugado! ");
            return false;
        }
    }

    /**
     * Retorna uma lista com todos os locais alugados de um cliente
     * Classe Local -> Número e Categoria
     * Para selecionar as outras infos do aluguel basta fazer uma busca no banco de dados
     * @param cpf - cpf do cliente
     * @return ArrayList<Local>
     */
    public ArrayList<Local> getListAlugadosPorCpf(String cpf) {
        ArrayList<Local> result = new ArrayList<Local>();
        String sql = "SELECT * FROM locaisAlugados WHERE cpf='" + cpf + "'";
        Cursor cursor = database.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int numero = cursor.getInt(0);
            result.add(new Local(numero));
        }
        return result;
    }

    /**
     * Pega um local da tabela locaisAlugados de acordo com o número
     * Retorna true caso ache o local e false caso contrário
     * @param numero - número do local
     * @return Aluguel
     */
    public Aluguel getLocalAlugado(String numero) {
        String sql = "SELECT * FROM locaisAlugados WHERE numero='" + numero + "'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            int number = cursor.getInt(0);
            String categoria = cursor.getString(1);
            String cpf = cursor.getString(2);
            String dataInicio = cursor.getString(3);
            String dataTermino = cursor.getString(4);
            int custoPorMes = cursor.getInt(5);
            int meses = cursor.getInt(6);
            int custoLocal = cursor.getInt(7);
            int custoOpcionais = cursor.getInt(8);
            int custoTotal = cursor.getInt(9);
            return new Aluguel(number, categoria, cpf, dataInicio, dataTermino, custoPorMes, meses, custoLocal, custoOpcionais, custoTotal);
        }
        return null;
    }

    /**
     * Retorna uma lista com o aluguel buscado pelo cliente
     * Para selecionar as outras infos do aluguel basta fazer uma busca no banco de dados
     * @param numero - número do aluguel
     * @param cpf - cpf do cliente
     * @return ArrayList<Aluguel>
     */
    public ArrayList<Aluguel> getListBuscaAlugadoPorCpf(String numero, String cpf) {
        ArrayList<Aluguel> result = new ArrayList<Aluguel>();
        String sql = "SELECT * FROM locaisAlugados WHERE numero='" + numero + "' AND cpf='" + cpf + "'";
        Cursor cursor = database.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int number = cursor.getInt(0);
            String categoria = cursor.getString(1);
            String cpF = cursor.getString(2);
            String dataInicio = cursor.getString(3);
            String dataTermino = cursor.getString(4);
            int custoPorMes = cursor.getInt(5);
            int meses = cursor.getInt(6);
            int custoLocal = cursor.getInt(7);
            int custoOpcionais = cursor.getInt(8);
            int custoTotal = cursor.getInt(9);
            result.add(new Aluguel(number, categoria, cpF, dataInicio, dataTermino, custoPorMes, meses, custoLocal, custoOpcionais, custoTotal));
        }
        return result;
    }

}
