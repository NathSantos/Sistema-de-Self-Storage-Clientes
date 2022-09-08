package br.edu.icomp.vaultstorage;

public class Aluguel {
    /**
     * número do local (identifica a categoria do local)
     * ------ TABELA DE NÚMEROS POR CATEGORIA -------
     * 	- guarda-volume -> {núm. 01 a 20} - 20 unidades
     *  - contêiner -> {núm. 21 a 50} - 30 unidades
     *  - quarto -> {núm. 51 a 90} - 40 unidades
     *  - galpão -> {núm. 91 a 100} - 10 unidades
     * ----------------------------------------------
     */
    private int numero;
    private String categoria;
    private String cpf;
    private String dataInicio;
    private String dataTermino;
    private int custoPorMes;
    private int meses;
    private int custoLocal; // custoPorMes * meses
    private int custoOpcionais;
    private int custoTotal;

    /**
     * Construtor da classe.
     */
    public Aluguel(int numero, String categoria, String cpf, String dataInicio, String dataTermino, int custoPorMes, int meses, int custoLocal, int custoOpcionais, int custoTotal) {
        this.numero = numero;
        this.categoria = categoria;
        this.cpf= cpf;
        this.dataInicio= dataInicio;
        this.dataTermino= dataTermino;
        this.custoPorMes= custoPorMes;
        this.meses= meses;
        this.custoLocal= custoLocal;
        this.custoOpcionais= custoOpcionais;
        this.custoTotal= custoTotal;
    }

    public int getNumero() {
        return numero;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getCpf() {
        return cpf;
    }
    public String getDataInicio() {
        return dataInicio;
    }
    public String getDataTermino() {
        return dataTermino;
    }
    public int getCustoPorMes() {
        return custoPorMes;
    }
    public int getMeses() {
        return meses;
    }
    public int getCustoLocal() {
        return custoLocal;
    }
    public int getCustoOpcionais() {
        return custoOpcionais;
    }
    public int getCustoTotal() {
        return custoTotal;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
    public void setCategoriaCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setCpf(String cpf) {
        this.cpf= cpf;
    }
    public void setDataInicio(String dataInicio) {
        this.dataInicio= dataInicio;
    }
    public void setDataTermino(String dataTermino) {
        this.dataTermino= dataTermino;
    }
    public void setCustoPorMes(int custoPorMes) {
        this.custoPorMes= custoPorMes;
    }
    public void setMeses(int meses) {
        this.meses= meses;
    }
    public void setCustoLocal(int custoLocal) {
        this.custoLocal= custoLocal;
    }
    public void setCustoOpcionais(int custoOpcionais) {
        this.custoOpcionais= custoOpcionais;
    }
    public void setCustoTotal(int custoTotal) {
        this.custoTotal= custoTotal;
    }
}
