package com.example.carrinhocerto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * TELA de cadastro de produto.
 * - Se receber um id pela Intent: modo EDITAR (mostra os dados e o botão Excluir).
 * - Se não receber id: modo NOVO (campos vazios).
 */
public class ProdutoActivity extends AppCompatActivity {

    private DatabaseHelper banco;
    private EditText editNome;
    private EditText editPreco;

    // Produto que está sendo editado (fica null quando é um produto novo)
    private Produto produtoEditando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        // Abre a conexão com o banco
        banco = new DatabaseHelper(this);

        // Busca os componentes do layout
        TextView txtTitulo = findViewById(R.id.txtTituloProduto);
        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnExcluir = findViewById(R.id.btnExcluir);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Verifica se a ListaActivity mandou o id de um produto (-1 = não mandou)
        long id = getIntent().getLongExtra(Formatador.CHAVE_ID_PRODUTO, -1);
        if (id != -1) {
            produtoEditando = banco.buscarPorId(id);
        }

        if (produtoEditando != null) {
            // MODO EDITAR: preenche os campos com os dados do banco
            txtTitulo.setText(R.string.titulo_editar_produto);
            editNome.setText(produtoEditando.getNome());
            // Mostra o preço com vírgula e 2 casas, ex.: 28,90
            editPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f",
                    produtoEditando.getPreco()));
            // O botão Excluir só aparece na edição
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            // MODO NOVO: campos vazios
            txtTitulo.setText(R.string.titulo_novo_produto);
        }

        // Ações dos botões
        btnSalvar.setOnClickListener(v -> salvar());
        btnExcluir.setOnClickListener(v -> confirmarExclusao());
        btnCancelar.setOnClickListener(v -> finish());
    }

    /**
     * Valida os campos e grava no banco (insere ou atualiza).
     */
    private void salvar() {
        String nome = editNome.getText().toString().trim();
        // Troca vírgula por ponto para o Java conseguir converter o número
        String textoPreco = editPreco.getText().toString().trim().replace(",", ".");

        // O nome é obrigatório
        if (nome.isEmpty()) {
            Toast.makeText(this, R.string.aviso_nome_produto, Toast.LENGTH_SHORT).show();
            return;
        }

        // O preço precisa ser um número maior que zero
        double preco;
        try {
            preco = Double.parseDouble(textoPreco);
        } catch (NumberFormatException e) {
            preco = 0;
        }
        if (preco <= 0) {
            Toast.makeText(this, R.string.aviso_preco_produto, Toast.LENGTH_SHORT).show();
            return;
        }

        if (produtoEditando == null) {
            // Produto novo: insere no banco
            banco.inserir(new Produto(nome, preco));
            Toast.makeText(this, R.string.msg_produto_cadastrado, Toast.LENGTH_SHORT).show();
        } else {
            // Produto existente: altera os dados e atualiza no banco
            produtoEditando.setNome(nome);
            produtoEditando.setPreco(preco);
            banco.atualizar(produtoEditando);
            Toast.makeText(this, R.string.msg_produto_atualizado, Toast.LENGTH_SHORT).show();
        }

        // Fecha a tela e volta para a lista (que recarrega no onResume)
        finish();
    }

    /**
     * Mostra uma caixa de diálogo perguntando se quer mesmo excluir.
     */
    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_confirmar_exclusao)
                .setMessage(getString(R.string.msg_confirmar_exclusao, produtoEditando.getNome()))
                // Botão "Sim": exclui do banco e volta para a lista
                .setPositiveButton(R.string.botao_sim, (dialogo, qual) -> {
                    banco.excluir(produtoEditando.getId());
                    Toast.makeText(this, R.string.msg_produto_excluido, Toast.LENGTH_SHORT).show();
                    finish();
                })
                // Botão "Não": só fecha a caixa de diálogo
                .setNegativeButton(R.string.botao_nao, null)
                .show();
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
