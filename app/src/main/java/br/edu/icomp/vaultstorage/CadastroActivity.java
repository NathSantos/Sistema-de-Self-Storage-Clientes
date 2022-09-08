package br.edu.icomp.vaultstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadastroActivity extends AppCompatActivity {
    private Cliente cliente = new Cliente();
    private EditText nomeEt;
    private EditText cpfEt;
    private EditText senhaEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeEt = (EditText) findViewById(R.id.editNomeCadastro);
        cpfEt = (EditText) findViewById(R.id.editCPFcadastro);
        senhaEt = (EditText) findViewById(R.id.editPassCadastro);

        Log.i("VaultStorage", "Activity de cadastro criada");
    }

    /**
     * Cadastra um novo usuário
     */
    public void cadastrarUsuario(View view) {
        // se a validação de campos estiver ok
        if(validaCampos() == true) {
            ClienteDAO clienteBD = new ClienteDAO(this);
            cliente.setNome(nomeEt.getText().toString());
            cliente.setCpf(cpfEt.getText().toString());
            cliente.setSenha(senhaEt.getText().toString());
            if (clienteBD.addCliente(cliente) == true) {
                String msg = "Bem-vindo(a) à Vault, " + cliente.getNome() + "!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                /** Passa as informações do usuário que fez o cadastro
                 * para a activity que contém os aluguéis desse cliente */
                Intent intent = new Intent(this, MeusLocaisAlugadosActivity.class);
                EditText inputNome = findViewById(R.id.editNomeCadastro);
                intent.putExtra("nome", inputNome.getText().toString());
                EditText inputCpf = findViewById(R.id.editCPFcadastro);
                intent.putExtra("cpf", inputCpf.getText().toString());
                startActivity(intent);
            }
        }
    }

    /**
     * Verificação de validação de campos
     */
    public boolean validaCampos() {
        boolean res = false;
        String nome = nomeEt.getText().toString();
        String cpf = cpfEt.getText().toString();
        String senha = senhaEt.getText().toString();

        if(res = isCampoVazio(nome)) { nomeEt.requestFocus(); }
        else {
           if(res = isCampoVazio(cpf)) { cpfEt.requestFocus(); }
           else {
               if(res = isCampoVazio(senha)) { senhaEt.requestFocus(); }
           }
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