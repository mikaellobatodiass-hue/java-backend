package com.example.carrinhocerto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo banco de dados SQLite do app.
 * Cria a tabela "produtos" e tem os métodos para
 * listar, buscar, inserir, atualizar e excluir produtos (CRUD).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nome do arquivo do banco e versão (aumente a versão se mudar a estrutura da tabela)
    private static final String NOME_BANCO = "carrinho_certo.db";
    private static final int VERSAO_BANCO = 1;

    // Nome da tabela e das colunas
    private static final String TABELA = "produtos";
    private static final String COLUNA_ID = "id";
    private static final String COLUNA_NOME = "nome";
    private static final String COLUNA_PRECO = "preco";

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    /**
     * Executado só uma vez, quando o banco é criado pela primeira vez.
     * Cria a tabela e já coloca os 10 produtos iniciais.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Comando SQL para criar a tabela
        db.execSQL("CREATE TABLE " + TABELA + " ("
                + COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUNA_NOME + " TEXT NOT NULL, "
                + COLUNA_PRECO + " REAL NOT NULL)");

        // Produtos iniciais da lista
        inserirInicial(db, "Arroz 5kg", 28.90);
        inserirInicial(db, "Feijão 1kg", 8.50);
        inserirInicial(db, "Café 500g", 18.90);
        inserirInicial(db, "Açúcar 5kg", 22.00);
        inserirInicial(db, "Óleo 900ml", 7.90);
        inserirInicial(db, "Leite 1L", 5.49);
        inserirInicial(db, "Carne 1kg", 42.90);
        inserirInicial(db, "Frango 1kg", 15.90);
        inserirInicial(db, "Macarrão 500g", 4.99);
        inserirInicial(db, "Sabão em pó 1kg", 14.90);
    }

    /**
     * Executado quando a versão do banco aumenta.
     * Aqui, de forma simples, apagamos a tabela e criamos de novo.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    // Insere um produto durante a criação do banco (usa o db recebido no onCreate)
    private void inserirInicial(SQLiteDatabase db, String nome, double preco) {
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_NOME, nome);
        valores.put(COLUNA_PRECO, preco);
        db.insert(TABELA, null, valores);
    }

    /**
     * Devolve todos os produtos do banco, em ordem de cadastro.
     */
    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Cursor funciona como um "ponteiro" que percorre as linhas do resultado
        Cursor cursor = db.query(TABELA, null, null, null, null, null, COLUNA_ID + " ASC");
        while (cursor.moveToNext()) {
            lista.add(lerProduto(cursor));
        }
        cursor.close();
        return lista;
    }

    /**
     * Busca um produto pelo id. Devolve null se não encontrar.
     */
    public Produto buscarPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA, null, COLUNA_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Produto produto = null;
        if (cursor.moveToFirst()) {
            produto = lerProduto(cursor);
        }
        cursor.close();
        return produto;
    }

    /**
     * Cadastra um produto novo. Devolve o id gerado (ou -1 se der erro).
     */
    public long inserir(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABELA, null, criarValores(produto));
    }

    /**
     * Salva as alterações de um produto que já existe.
     */
    public void atualizar(Produto produto) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABELA, criarValores(produto), COLUNA_ID + " = ?",
                new String[]{String.valueOf(produto.getId())});
    }

    /**
     * Apaga um produto do banco pelo id.
     */
    public void excluir(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABELA, COLUNA_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Monta o "pacote" de valores (nome e preço) para gravar no banco
    private ContentValues criarValores(Produto produto) {
        ContentValues valores = new ContentValues();
        valores.put(COLUNA_NOME, produto.getNome());
        valores.put(COLUNA_PRECO, produto.getPreco());
        return valores;
    }

    // Transforma a linha atual do cursor em um objeto Produto
    private Produto lerProduto(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUNA_ID));
        String nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUNA_NOME));
        double preco = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUNA_PRECO));
        return new Produto(id, nome, preco);
    }
}
