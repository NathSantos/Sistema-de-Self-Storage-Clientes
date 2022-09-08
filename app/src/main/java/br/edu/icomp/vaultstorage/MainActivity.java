package br.edu.icomp.vaultstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText cpfEt;
    private EditText senhaEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpfEt = (EditText) findViewById(R.id.editCPFlogin);
        senhaEt = (EditText) findViewById(R.id.editPassLogin);

        Log.i("VaultStorage", "Activity principal criada");
    }

    /**
     * Verifica a existência dos dados do usuário no banco de dados; se existir, loga na conta
     */
    public void logarCliente(View view) {
        // se a validação de campos estiver ok
        if (validaCampos() == true) {
            ClienteDAO clienteBD = new ClienteDAO(this);
            String cpfInformado = cpfEt.getText().toString();
            String senhaInformada = senhaEt.getText().toString();

            // se o cliente existir no banco de dados
            if (clienteBD.getCliente(cpfInformado) != null) {
                Cliente cliente = clienteBD.getCliente(cpfInformado);

                // se a senha estiver correta
                if(clienteBD.validaSenha(cpfInformado, senhaInformada) == true) {
                    String msg = "Bem-vindo(a) à Vault, " + clienteBD.getCliente(cpfInformado).getNome() + "!";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                    /** Passa as informações do usuário que fez o login
                    * para a activity que contém os aluguéis desse cliente */
                    Intent intent = new Intent(this, MeusLocaisAlugadosActivity.class);
                    intent.putExtra("cpf", cpfInformado);
                    intent.putExtra("nome", clienteBD.getCliente(cpfInformado).getNome());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Senha Incorreta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Usuário não cadastrado", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Verificação de validação de campos
     */
    public boolean validaCampos() {
        boolean res = false;
        String cpf = cpfEt.getText().toString();
        String senha = senhaEt.getText().toString();

        if(res = isCampoVazio(cpf)) { cpfEt.requestFocus(); }
        else {
            if(res = isCampoVazio(senha)) { senhaEt.requestFocus(); }
        }

        if(res == true) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Aviso");
            dlg.setMessage("Há campos iválidos ou em branco!");
            dlg.setNeutralButton("OK", null);
            dlg.show();
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Verifica se o campo passado como parâmetro está vazio
     */
    private boolean isCampoVazio(String valor){
        boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
        return resultado;
    }

    /**
     * Abre a activity de cadastro do cliente
     */
    public void cadastrarClicado(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    /**
     * Cria o menu na activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Opções do Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("A Vault Storage é uma empresa de Auto-Serviço de Armazenamento brasileira.\nCom mais de 5 anos de atuação no mercado, garantimos um serviço de self storage tranquilo e seguro para você!\nContamos com diversas categorias que atendem muito bem a cada finalidade que você precise, além de estruturas protegidas por sistemas avançados de segurança.\nTodas as nossas instalações ficam em: Rua Maltês, Bloco 54.\nPara mais informações, contate nossa central: 0800 3655 2001\n")
                        .setNeutralButton("Ok", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}