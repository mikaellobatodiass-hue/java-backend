package com.example.carrinhocerto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TELA 2 - Lista de Compras.
 * Carrega os produtos do banco SQLite, mostra cada um com CheckBox e botão Editar,
 * e soma o total dos marcados na hora.
 */
public class ListaActivity extends AppCompatActivity {

    // Acesso ao banco de dados
    private DatabaseHelper banco;

    // Produtos carregados do banco
    private List<Produto> produtos = new ArrayList<>();

    // Guarda os ids dos produtos marcados, para não perder a marcação
    // quando a lista é recarregada (ex.: ao voltar da tela de edição)
    private final Set<Long> idsMarcados = new HashSet<>();

    // Valores usados na tela
    private double orcamento;
    private double total = 0;

    // Componentes da tela
    private LinearLayout containerProdutos;
    private TextView txtTotal;
    private TextView txtQtdItens;
    private TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        // Abre a conexão com o banco
        banco = new DatabaseHelper(this);

        // Recebe o orçamento enviado pela MainActivity
        orcamento = getIntent().getDoubleExtra(Formatador.CHAVE_ORCAMENTO, 0);

        // Busca os componentes do layout
        TextView txtOrcamento = findViewById(R.id.txtOrcamento);
        containerProdutos = findViewById(R.id.containerProdutos);
        txtTotal = findViewById(R.id.txtTotal);
        txtQtdItens = findViewById(R.id.txtQtdItens);
        txtVazio = findViewById(R.id.txtVazio);
        Button btnNovoProduto = findViewById(R.id.btnNovoProduto);
        Button btnVerResumo = findViewById(R.id.btnVerResumo);

        // Mostra o orçamento no cabeçalho
        txtOrcamento.setText(getString(R.string.orcamento_valor, Formatador.emReais(orcamento)));

        // Botão "Novo Produto": abre a ProdutoActivity sem id (modo cadastro)
        btnNovoProduto.setOnClickListener(v ->
                startActivity(new Intent(this, ProdutoActivity.class)));

        // Botão "Ver Resumo": abre a ResumoActivity enviando orçamento e total
        btnVerResumo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResumoActivity.class);
            intent.putExtra(Formatador.CHAVE_ORCAMENTO, orcamento);
            intent.putExtra(Formatador.CHAVE_TOTAL, total);
            startActivity(intent);
        });
    }

    /**
     * onResume roda sempre que a tela aparece: ao abrir e também ao voltar
     * da tela de cadastro/edição. Por isso recarregamos a lista aqui.
     */
    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    /**
     * Busca os produtos no banco e monta a lista na tela.
     */
    private void carregarProdutos() {
        produtos = banco.listarProdutos();

        // Limpa os itens antigos antes de montar de novo
        containerProdutos.removeAllViews();

        // Ids que ainda existem no banco (para tirar da marcação os que foram excluídos)
        Set<Long> idsExistentes = new HashSet<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Produto produto : produtos) {
            idsExistentes.add(produto.getId());

            // Cria a linha a partir do layout item_produto.xml
            View item = inflater.inflate(R.layout.item_produto, containerProdutos, false);
            CheckBox checkProduto = item.findViewById(R.id.checkProduto);
            TextView txtNomeProduto = item.findViewById(R.id.txtNomeProduto);
            TextView txtPrecoProduto = item.findViewById(R.id.txtPrecoProduto);
            Button btnEditar = item.findViewById(R.id.btnEditar);

            // Preenche o nome e o preço do produto, ex.: "Arroz 5kg" e "R$ 28,90"
            txtNomeProduto.setText(produto.getNome());
            txtPrecoProduto.setText(Formatador.emReais(produto.getPreco()));

            // Tocar em qualquer parte do cartão também marca/desmarca o produto
            item.setOnClickListener(v -> checkProduto.toggle());

            // Se o produto já estava marcado antes, continua marcado
            checkProduto.setChecked(idsMarcados.contains(produto.getId()));

            // Ao marcar/desmarcar: guarda o id e recalcula o total
            checkProduto.setOnCheckedChangeListener((botao, marcado) -> {
                if (marcado) {
                    idsMarcados.add(produto.getId());
                } else {
                    idsMarcados.remove(produto.getId());
                }
                atualizarTotal();
            });

            // Botão "Editar": abre a ProdutoActivity enviando o id deste produto
            btnEditar.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProdutoActivity.class);
                intent.putExtra(Formatador.CHAVE_ID_PRODUTO, produto.getId());
                startActivity(intent);
            });

            containerProdutos.addView(item);
        }

        // Remove da marcação os produtos que foram excluídos do banco
        idsMarcados.retainAll(idsExistentes);

        // Se não houver produtos, mostra um aviso e esconde o cartão da lista
        if (produtos.isEmpty()) {
            txtVazio.setVisibility(View.VISIBLE);
            containerProdutos.setVisibility(View.GONE);
        } else {
            txtVazio.setVisibility(View.GONE);
            containerProdutos.setVisibility(View.VISIBLE);
        }

        atualizarTotal();
    }

    /**
     * Soma o preço de todos os produtos marcados e mostra no rodapé,
     * junto com a quantidade de itens marcados (ex.: "3 de 10 itens").
     */
    private void atualizarTotal() {
        total = 0;
        int quantidade = 0;
        for (Produto produto : produtos) {
            if (idsMarcados.contains(produto.getId())) {
                total += produto.getPreco();
                quantidade++;
            }
        }
        txtTotal.setText(Formatador.emReais(total));
        txtQtdItens.setText(getString(R.string.qtd_itens, quantidade, produtos.size()));
    }

    /**
     * Quando a tela é destruída, fecha a conexão com o banco.
     */
    @Override
    protected void onDestroy() {
        banco.close();
        super.onDestroy();
    }
}
