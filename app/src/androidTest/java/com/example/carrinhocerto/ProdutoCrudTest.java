package com.example.carrinhocerto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Teste do CRUD de produtos no banco SQLite.
 * Roda no emulador/celular, pois usa o banco de verdade do Android.
 *
 * Em um único teste passamos pelas 4 operações:
 * C = Create (inserir), R = Read (ler/listar), U = Update (atualizar), D = Delete (excluir).
 * No final o produto de teste é excluído, então o banco fica como estava.
 */
@RunWith(AndroidJUnit4.class)
public class ProdutoCrudTest {

    @Test
    public void crudCompletoDeProduto() {
        // Pega o contexto do app e abre o banco
        Context contexto = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper banco = new DatabaseHelper(contexto);

        // Quantidade de produtos antes do teste
        int quantidadeAntes = banco.listarProdutos().size();

        // ---------- C: CREATE (inserir) ----------
        long id = banco.inserir(new Produto("Produto Teste", 10.50));
        assertTrue("O insert deveria devolver um id válido", id > 0);

        // ---------- R: READ (buscar e listar) ----------
        Produto lido = banco.buscarPorId(id);
        assertNotNull("O produto inserido deveria ser encontrado", lido);
        assertEquals("Produto Teste", lido.getNome());
        assertEquals(10.50, lido.getPreco(), 0.001);

        List<Produto> lista = banco.listarProdutos();
        assertEquals("A lista deveria ter 1 produto a mais",
                quantidadeAntes + 1, lista.size());

        // ---------- U: UPDATE (atualizar) ----------
        lido.setNome("Produto Teste Editado");
        lido.setPreco(12.99);
        banco.atualizar(lido);

        Produto atualizado = banco.buscarPorId(id);
        assertNotNull(atualizado);
        assertEquals("Produto Teste Editado", atualizado.getNome());
        assertEquals(12.99, atualizado.getPreco(), 0.001);

        // ---------- D: DELETE (excluir) ----------
        banco.excluir(id);

        assertNull("O produto excluído não deveria mais existir", banco.buscarPorId(id));
        assertEquals("A lista deveria voltar ao tamanho original",
                quantidadeAntes, banco.listarProdutos().size());

        banco.close();
    }
}
