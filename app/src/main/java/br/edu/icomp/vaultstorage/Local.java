package br.edu.icomp.vaultstorage;

/**
 * Classe Local - Representa um local na aplicação
 * @author Nathália Santos &lt;nathsantos2703@gmail.com&gt;
 * @version 1.0, 2022-08-06
 */
public class Local {
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

    /**
     * Construtor da classe.
     * @param numero - número do local
     */
    public Local(int numero) {
        this.numero = numero;
    }

    /**
     * Pega a categoria (classe Categoria) do local
     * @return Categoria categoria
     */
    public Categoria getCategoria() {
        String categoria = null;
        if (numero >= 1 && numero <= 20) {
            categoria = "guarda-volumes";
        }
        else if (numero >= 21 && numero <= 50) {
            categoria = "contêiner";
        }
        else if (numero >= 51 && numero <= 90) {
            categoria = "quarto";
        }
        else if (numero >= 91 && numero <= 100) {
            categoria = "galpão";
        }
        Categoria cat = new Categoria(categoria);
        return cat;
    }

    /**
     * Pega o endereço do local (todos possuem o mesmo endereço)
     * @return String endereço
     */
    public String getEndereco() {
        return "Rua Maltês, bloco 54";
    }

    /**
     * Muda o número do local (void)
     * @param numero - número do local
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * Pega o número do local
     * @return int numero
     */
    public int getNumero() {
        return numero;
    }
}
