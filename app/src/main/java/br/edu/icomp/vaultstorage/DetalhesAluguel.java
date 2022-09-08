package br.edu.icomp.vaultstorage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetalhesAluguel extends AppCompatActivity {
    private String cpfAtual, numeroAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aluguel);
        Intent intent = getIntent();
        cpfAtual = intent.getStringExtra("cpf");

        // Text View Título com número do aluguel
        TextView textTituloAluguel = findViewById(R.id.textViewTituloDetalheAluguel);
        numeroAtual = intent.getStringExtra("numeroAluguel");
        numeroAtual = numeroAtual.replace("número: ", "");
        textTituloAluguel.setText("Número " + numeroAtual);

        LocaisDAO locaisBD = new LocaisDAO(this);
        Aluguel aluguel = locaisBD.getLocalAlugado(numeroAtual);

        // Text Views Infos do aluguel
        TextView textViewCategoria = findViewById(R.id.textDetalheCategoria);
        textViewCategoria.setText("Categoria: " + aluguel.getCategoria());
        TextView textViewDataInicio = findViewById(R.id.textDetalheDataInicio);
        String dataInicio = aluguel.getDataInicio().replace("-", "/");
        textViewDataInicio.setText("Data de Início: " + dataInicio);
        TextView textViewDataTermino = findViewById(R.id.textDetalheDataTermino);
        String dataTermino = aluguel.getDataTermino().replace("-", "/");
        textViewDataTermino.setText("Data de Término: " + dataTermino);
        TextView textViewMeses = findViewById(R.id.textDetalheMeses);
        textViewMeses.setText("Meses de aluguel: " + aluguel.getMeses());
        TextView textViewCustoMes = findViewById(R.id.textDetalheCustoPorMes);
        textViewCustoMes.setText("Custo por mês: R$" + aluguel.getCustoPorMes() + ",00");
        TextView textViewCustoLocal = findViewById(R.id.textDetalheCustolocal);
        textViewCustoLocal.setText("Custo durante aluguel: R$" + aluguel.getCustoLocal() + ",00");
        TextView textViewCustoOpc = findViewById(R.id.textDetalheCustoOpc);
        textViewCustoOpc.setText("Custo opcionais: R$" + aluguel.getCustoOpcionais() + ",00");
        TextView textViewCustoTotal = findViewById(R.id.textDetalheCustoTotal);
        textViewCustoTotal.setText("Total: R$" + aluguel.getCustoTotal() + ",00");
    }

    /**
     * Método a ser chamado quando o botão de cancelar aluguel for acionado
     */
    public void exibirConfirmacaoCancelamento(View view) {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Cancelamento");
        msgBox.setMessage("Tem certeza de que deseja cancelar esse aluguel?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LocaisDAO locaisBD = new LocaisDAO(DetalhesAluguel.this);
                if (locaisBD.cancelaAluguel(cpfAtual, numeroAtual) == true) {
                    Toast.makeText(DetalhesAluguel.this, "Aluguel cancelado com sucesso!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetalhesAluguel.this, MeusLocaisAlugadosActivity.class);
                    intent.putExtra("cpf", cpfAtual);
                    ClienteDAO clientesBD = new ClienteDAO(DetalhesAluguel.this);
                    intent.putExtra("nome", clientesBD.getCliente(cpfAtual).getNome());
                    startActivity(intent);
                } else {
                    Toast.makeText(DetalhesAluguel.this, "Não foi possível cancelar o aluguel", Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        msgBox.show();
    }

    /**
     * Verifica se a data de início foi escolhida e se é uma data válida
     */
    public boolean validaDataInicio(String dataIni, String dataTer) throws ParseException {
        Date dataIniFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataIni);
        Date dataTerFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataTer);
        String dataHoje = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Date hoje = new SimpleDateFormat("yyyy-MM-dd").parse(dataHoje);
        if((dataIniFormatada != null) && (dataIniFormatada.after(hoje)) && (dataIniFormatada.before(dataTerFormatada))) {
            return true;
        } else { return false; }
    }

    /**
     * Verifica se a data de término foi escolhida e se é uma data válida
     */
    public boolean validaDataTermino(String dataTer, String dataIni) throws ParseException {
        Date dataIniFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataIni);
        Date dataTerFormatada = new SimpleDateFormat("yyyy-MM-dd").parse(dataTer);
        String dataHoje = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Date hoje = new SimpleDateFormat("yyyy-MM-dd").parse(dataHoje);
        if((dataTerFormatada != null) && (dataTerFormatada.after(hoje)) && (dataTerFormatada.after(dataIniFormatada))) {
            return true;
        } else { return false; }
    }

    /**
     * Método a ser chamado quando o botão de alterar datas for acionado
     */
    public void alterarDatas(View view) {
        Calendar calendar = Calendar.getInstance();
        final int ano = calendar.get(Calendar.YEAR);
        final int mes = calendar.get(Calendar.MONTH);
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);
        AlertDialog.Builder msgBox = new AlertDialog.Builder(DetalhesAluguel.this);
        msgBox.setTitle("Mudança de datas");
        msgBox.setMessage("Qual data você deseja alterar?");
        msgBox.setPositiveButton("Término", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePickerDialog dateIniPickerDialog = new DatePickerDialog(
                        DetalhesAluguel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                        mes = mes + 1;
                        String strDataTermino = ano + "-" + mes + "-" + dia;
                        try {
                            LocaisDAO locaisBD = new LocaisDAO(DetalhesAluguel.this);
                            if (validaDataTermino(strDataTermino, locaisBD.getDataInicio(numeroAtual)) == false) {
                                Toast.makeText(DetalhesAluguel.this, "Data Inválida!!", Toast.LENGTH_SHORT).show();
                            } else {
                                locaisBD.alteraDataAluguel(cpfAtual, numeroAtual, locaisBD.getDataInicio(numeroAtual), strDataTermino);
                                Intent intent = new Intent(DetalhesAluguel.this, DetalhesAluguel.class);
                                intent.putExtra("cpf", cpfAtual);
                                intent.putExtra("numeroAluguel", "número: "+numeroAtual);
                                startActivity(intent);
                                Toast.makeText(DetalhesAluguel.this, "Data de término alterada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, ano, mes, dia);
                dateIniPickerDialog.show();
            }
        });
        msgBox.setNegativeButton("Início", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePickerDialog dateIniPickerDialog = new DatePickerDialog(
                        DetalhesAluguel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                        mes = mes + 1;
                        String strDataInicio = ano + "-" + mes + "-" + dia;
                        try {
                            LocaisDAO locaisBD = new LocaisDAO(DetalhesAluguel.this);
                            if (validaDataInicio(strDataInicio, locaisBD.getDataTermino(numeroAtual)) == false) {
                                Toast.makeText(DetalhesAluguel.this, "Data Inválida!!", Toast.LENGTH_SHORT).show();
                            } else {
                                locaisBD.alteraDataAluguel(cpfAtual, numeroAtual, strDataInicio, locaisBD.getDataTermino(numeroAtual));
                                Intent intent = new Intent(DetalhesAluguel.this, DetalhesAluguel.class);
                                intent.putExtra("cpf", cpfAtual);
                                intent.putExtra("numeroAluguel", "número: "+numeroAtual);
                                startActivity(intent);
                                Toast.makeText(DetalhesAluguel.this, "Data de início alterada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, ano, mes, dia);
                dateIniPickerDialog.show();
            }
        });
        msgBox.show();
    }

    /**
     * Cria o menu na activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    /**
     * Opções do Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuVoltar:
                Intent intent = new Intent(this, MeusLocaisAlugadosActivity.class);
                intent.putExtra("cpf", cpfAtual);
                ClienteDAO clientesBD = new ClienteDAO(DetalhesAluguel.this);
                intent.putExtra("nome", clientesBD.getCliente(cpfAtual).getNome());
                startActivity(intent);
                return true;
            case R.id.menuInfoCategorias:
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
                            "\n----------------------------------------------";}
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
                alert.setMessage(info).setNeutralButton("Ok", null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
