package br.edu.icomp.vaultstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NovoAluguel extends AppCompatActivity {
    private CheckBox checkSeguro, checkChave, checkControle;
    EditText etDataInicio, etDataTermino, etNumeroCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_aluguel);

        // Edit Text das categorias
        etNumeroCategoria = findViewById(R.id.editTextNumberCategoria);

        // CheckBox dos opcionais
        checkSeguro = findViewById(R.id.checkBoxSeguro);
        checkChave = findViewById(R.id.checkBoxChave);
        checkControle = findViewById(R.id.checkBoxControle);

        // Datas de Início e Término do aluguel
        etDataInicio = findViewById(R.id.editTextDataInicio);
        etDataTermino = findViewById(R.id.editTextDataTermino);
        Calendar calendar = Calendar.getInstance();
        final int ano = calendar.get(Calendar.YEAR);
        final int mes = calendar.get(Calendar.MONTH);
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);
        etDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NovoAluguel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                        mes = mes+1;
                        String strDataInicio = ano + "-" + mes + "-" + dia;
                        etDataInicio.setText(strDataInicio);
                    }
                },ano,mes,dia);
                datePickerDialog.show();
            }
        });
        etDataTermino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NovoAluguel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                        mes = mes+1;
                        String strDataTermino = ano + "-" + mes + "-" + dia;
                        etDataTermino.setText(strDataTermino);
                    }
                },ano,mes,dia);
                datePickerDialog.show();
            }
        });

        Log.i("VaultStorage", "Activity Novo Aluguel criada!");
    }

    /**
     * Método a ser chamado quando o botão de finalizar for acionado
     */
    public void finalizar(View view) throws ParseException {
        Intent intent = getIntent();
        String cpf = intent.getStringExtra("cpf");
        String nome = intent.getStringExtra("nome");

        // ---------- EditText (categorias) ----------
        String valorCat = etNumeroCategoria.getText().toString();
        int cat = Integer.parseInt(valorCat);
        String categoriaSelecionada = null;
        if(cat == 1) {categoriaSelecionada = "guarda-volumes";}
        else if(cat == 2) {categoriaSelecionada = "contêiner";}
        else if(cat == 3) {categoriaSelecionada = "quarto";}
        else if(cat == 4) {categoriaSelecionada = "galpão";}
        System.out.println(" CATEGORIA " + categoriaSelecionada);
        // ---------- Checkboxs (opcionais) ----------
        boolean seguroIsChecked = checkSeguro.isChecked();
        boolean chaveIsChecked = checkChave.isChecked();
        boolean controleIsChecked = checkControle.isChecked();
        // ---------- Datas de Início e Término do aluguel ----------
        String strDataInicio = etDataInicio.getText().toString();
        String strDataTermino = etDataTermino.getText().toString();

        // ---------- verificação das datas e, caso esteja tudo certo, procura disponibilidade e aluga ----------
        if ((etDataInicio.getText().length() == 0) || (etDataTermino.getText().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Selecione as datas!!", Toast.LENGTH_SHORT).show();
        } else {
            if (validaDataInicio(strDataInicio)) {
                if (validaDataTermino(strDataInicio, strDataTermino)) {
                    LocaisDAO locaisBD = new LocaisDAO(this);
                    if (locaisBD.procuraDisponibilidadeEAluga(cpf, categoriaSelecionada, strDataInicio, strDataTermino, seguroIsChecked, chaveIsChecked, controleIsChecked)) {
                        Toast.makeText(getApplicationContext(), "Temos disponibilidade!\nLocal Alugado com Sucesso!!", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(this, MeusLocaisAlugadosActivity.class);
                        intent2.putExtra("nome", nome);
                        intent2.putExtra("cpf", cpf);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sem disponibilidade para\na categoria escolhida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data de término inválida!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Data de início inválida!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verifica se a data de início foi escolhida e se é uma data válida
     */
    public boolean validaDataInicio(String dataIni) throws ParseException {
        Date dataIniFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataIni);
        String dataHoje = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Date hoje = new SimpleDateFormat("yyyy-MM-dd").parse(dataHoje);
        if((dataIniFormatada != null) && (dataIniFormatada.after(hoje))) {
            return true;
        } else { return false; }
    }

    /**
     * Verifica se a data de término foi escolhida e se é uma data válida
     */
    public boolean validaDataTermino(String dataIni, String dataTer) throws ParseException {
        Date dataIniFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataIni);
        Date dataTerFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataTer);
        String dataHoje = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Date hoje = new SimpleDateFormat("yyyy-MM-dd").parse(dataHoje);
        if((dataIniFormatada != null) && (dataTerFormatada != null) && (dataTerFormatada.after(hoje)) && (dataTerFormatada.after(dataIniFormatada))) {
            return true;
        } else { return false; }
    }

    /**
     * Método que exibe as informações das categorias
     */
    public void infoCategorias(View view) {
        String[] listaCategorias = {"guarda-volumes", "contêiner", "quarto", "galpão"};
        String info = "CATEGORIAS DISPONÍVEIS:\n----------------------------------------------";

        for(int i=0;i<4;i++) {
            Categoria categoria = new Categoria(listaCategorias[i]);
            info += "\n--> " + categoria.getCategoria() +
                    "\n  Custo por Mês: R$" + categoria.getCusto() + ",00" +
                    "\n  Comprimento: " + categoria.getComprimento() + "m" +
                    "\n  Largura: " + categoria.getLargura() + "m" +
                    "\n  Altura: " + categoria.getAltura() + "m" +
                    "\n  Volume: " + categoria.getVolume() + "m^3" +
                    "\n----------------------------------------------";
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(info)
                .setNeutralButton("Ok", null)
                .show();
    }
}