package br.edu.icomp.vaultstorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.widget.SearchView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

public class MeusLocaisAlugadosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LocalsAdapter adapter;
    private String cpfAtual;
    private SearchView svBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_locais_alugados);

        // Mensagem de Bem-vindo
        TextView textBemVindo = findViewById(R.id.textViewBoasVindasCad);
        Intent intent = getIntent();
        String login = intent.getStringExtra("nome");
        cpfAtual = intent.getStringExtra("cpf");
        textBemVindo.setText("Olá, " + login + "!");
        TextView textQuantAlugueis = findViewById(R.id.textQuantAlugueis);
        LocaisDAO locaisBD = new LocaisDAO(this);
        textQuantAlugueis.setText("Você possui " + locaisBD.quantAlugadosPorCpf(intent.getStringExtra("cpf")) + " aluguéis");

        // Adaptador para a lista de aluguéis do cliente
        recyclerView = findViewById(R.id.list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocalsAdapter(this);
        recyclerView.setAdapter(adapter);

        svBusca = findViewById(R.id.searchView);
        svBusca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // BUSCAR A LISTA COM A QUERY DESEJADA
                RecyclerView recyclerView2 = (RecyclerView) recyclerView.findViewById(R.id.list_recycler);
                if(query.isEmpty()) {
                    recyclerView2.setAdapter(adapter);
                } else {
                    recyclerView2.setAdapter(new LocalsAdapterBusca(MeusLocaisAlugadosActivity.this, query));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Log.i("VaultStorage", "Activity Meus Aluguéis criada!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.update();
        adapter.notifyDataSetChanged();
    }

    /**
     * Atualiza a recycler view para os casos de busca que não voltam para todos os aluguéis após a busca ter sido feita
     */
    public void reloadReciclerView(View view) {
        RecyclerView recyclerView3 = (RecyclerView) recyclerView.findViewById(R.id.list_recycler);
        recyclerView3.setAdapter(adapter);
    }

    /**
     * Abre a activity de novo aluguel
     */
    public void novoAluguel(View view) {
        Intent intent2 = getIntent();
        Intent intent = new Intent(this, NovoAluguel.class);
        intent.putExtra("nome", intent2.getStringExtra("nome"));
        intent.putExtra("cpf", intent2.getStringExtra("cpf"));
        startActivity(intent);
    }

    /**
     * Cria o menu na activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    /**
     * Opções do Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuSobre:
                AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
                alert3.setMessage("A Vault Storage é uma empresa de Auto-Serviço de Armazenamento brasileira.\nCom mais de 5 anos de atuação no mercado, garantimos um serviço de self storage tranquilo e seguro para você!\nContamos com diversas categorias que atendem muito bem a cada finalidade que você precise, além de estruturas protegidas por sistemas avançados de segurança.\nTodas as nossas instalações ficam em: Rua Maltês, Bloco 54.\nPara mais informações, contate nossa central: 0800 3655 2001\n")
                        .setNeutralButton("Ok", null)
                        .show();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(info).setNeutralButton("Ok", null).show();
                return true;
            case R.id.menuExcluirConta:
                AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
                alert2.setTitle("Você tem certeza de que deseja excluir sua conta?");
                alert2.setMessage("Ao fazer isso, todos os seus aluguéis serão cancelados.");
                alert2.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClienteDAO clientesBD = new ClienteDAO(MeusLocaisAlugadosActivity.this);
                        LocaisDAO locaisBD = new LocaisDAO(MeusLocaisAlugadosActivity.this);
                        if(clientesBD.removerCliente(cpfAtual)) {
                            locaisBD.cancelaTodosAlugueisPorCpf(cpfAtual);
                            AlertDialog.Builder alert3 = new AlertDialog.Builder(MeusLocaisAlugadosActivity.this);
                            alert3.setTitle("Conta excluída.");
                            alert3.setMessage("Sentiremos sua falta!  :(");
                            alert3.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MeusLocaisAlugadosActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            alert3.show();
                        } else {
                            Toast.makeText(MeusLocaisAlugadosActivity.this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert2.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}});
                alert2.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    /**
     * Classe adapter para lista com os aluguéis daquele cliente
     */
    class LocalsAdapter extends RecyclerView.Adapter<LocalsViewHolder> {
        private Context context;
        private ArrayList<Local> locais;
        LocaisDAO locaisDAO;

        public LocalsAdapter(Context context) {
            this.context = context;
            locaisDAO = new LocaisDAO(context);
            update();
        }

        public void update() {
            locais = locaisDAO.getListAlugadosPorCpf(cpfAtual);
        }

        public LocalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            LocalsViewHolder vh = new LocalsViewHolder(v, context);
            return vh;
        }

        public void onBindViewHolder(LocalsViewHolder holder, int position) {
            holder.numero.setText("número: " + locais.get(position).getNumero());
            holder.categoria.setText("Categoria: " + locais.get(position).getCategoria().getCategoria());
        }

        public int getItemCount() { return locais.size(); }
    }

    /**
     * Classe Holder para classe do adapter
     */
    class LocalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Context context;
        public TextView numero, categoria;

        public LocalsViewHolder(ConstraintLayout v, Context context) {
            super(v);
            this.context = context;
            numero = v.findViewById(R.id.itemNumeroAluguel);        // numero em String
            categoria = v.findViewById(R.id.itemCategoriaAluguel);
            v.setOnClickListener(this);
        }

        public void onClick(View v) {
            String numero = this.numero.getText().toString();
            detalhesAluguel(v, numero);
        }
    }

    /**
     * Classe adapter para lista com a busca dos locais alugados daquele cliente
     */
    class LocalsAdapterBusca extends RecyclerView.Adapter<LocalsViewHolder> {
        private String query;
        private Context context;
        private ArrayList<Aluguel> locais;
        LocaisDAO locaisDAO;

        public LocalsAdapterBusca(Context context, String query) {
            this.context = context;
            this.query = query;
            locaisDAO = new LocaisDAO(context);
            update();
        }

        public void update() {locais = locaisDAO.getListBuscaAlugadoPorCpf(query, cpfAtual);}

        public LocalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            LocalsViewHolder vh = new LocalsViewHolder(v, context);
            return vh;
        }

        public void onBindViewHolder(LocalsViewHolder holder, int position) {
            holder.numero.setText("número: " + locais.get(position).getNumero());
            holder.categoria.setText("Categoria: " + locais.get(position).getCategoria());
        }

        public int getItemCount() { return locais.size(); }
    }

    /**
     * Abre a activity de Detalhes de cada Aluguel
     */
    public void detalhesAluguel(View view, String numeroAluguel) {
        Intent intent = new Intent(this, DetalhesAluguel.class);
        intent.putExtra("cpf", cpfAtual);
        intent.putExtra("numeroAluguel", numeroAluguel);
        startActivity(intent);
    }

}



