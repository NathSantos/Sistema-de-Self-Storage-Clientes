package br.edu.icomp.vaultstorage;

/**
 * Classe Cliente - Representa um cliente na aplicação
 * @author Nathália Santos &lt;nathsantos2703@gmail.com&gt;
 * @version 1.0, 2022-08-06
 */
public class Cliente {
    /**
     * Nome do cliente
     * CPF do cliente
     * Senha do cliente
     */
    private String nome;
    private String cpf;
    private String senha;

    /**
     * Construtor da classe.
     * @param nome - nome do cliente
     * @param cpf - cpf do cliente
     * @param senha - senha do cliente
     */
    public Cliente(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
    }

    /**
     * Segundo construtor da classe (sem parâmetros).
     */
    public Cliente() {}

    /**
     * Pega o nome do cliente
     * @return String nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Muda o nome do cliente (void)
     * @param nome - nome do cliente
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Pega o cpf do cliente
     * @return String cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Muda o cpf do cliente (void)
     * @param cpf - cpf do cliente
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Pega a senha do cliente
     * @return String senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Muda a senha do cliente (void)
     * @param senha - senha do cliente
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
