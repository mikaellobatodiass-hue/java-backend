package com.example.carrinhocerto;

/**
 * Classe modelo: representa um produto do mercado.
 * Cada objeto Produto corresponde a uma linha da tabela "produtos" do banco.
 */
public class Produto {

    private long id;        // código do produto no banco (gerado automaticamente)
    private String nome;    // nome do produto, ex.: "Arroz 5kg"
    private double preco;   // preço do produto, ex.: 28.90

    // Construtor usado para criar um produto novo (ainda sem id no banco)
    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // Construtor usado quando o produto vem do banco (já tem id)
    public Produto(long id, String nome, double preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    // Getters e setters: métodos para ler e alterar os atributos

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
