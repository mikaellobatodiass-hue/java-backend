package com.example.carrinhocerto;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Teste de TELA (front-end) do CRUD de produtos, usando Espresso.
 * O Espresso "usa" o app como uma pessoa: digita nos campos, toca nos botões
 * e confere o que aparece na tela.
 *
 * Caminho testado:
 * Início → Lista → Novo Produto (cadastrar) → Editar (alterar) → Editar → Excluir (confirmar "Sim").
 * No final o produto de teste é excluído pela própria tela, então o banco fica como estava.
 */
@RunWith(AndroidJUnit4.class)
public class ProdutoTelaTest {

    @Test
    public void crudPelosBotoesDaTela() {
        // Nome único (com horário) para não confundir com outros produtos da lista
        String nome = "Teste Tela " + System.currentTimeMillis();
        String nomeEditado = nome + " Editado";

        // Abre o app na tela inicial
        try (ActivityScenario<MainActivity> tela = ActivityScenario.launch(MainActivity.class)) {

            // ---------- Tela inicial: digita o orçamento e toca em "Começar" ----------
            onView(withId(R.id.editOrcamento)).perform(replaceText("500"), closeSoftKeyboard());
            onView(withId(R.id.btnComecar)).perform(click());

            // ---------- C: cadastrar pelo botão "+ Novo Produto" ----------
            onView(withId(R.id.btnNovoProduto)).perform(scrollTo(), click());
            onView(withId(R.id.editNome)).perform(replaceText(nome), closeSoftKeyboard());
            onView(withId(R.id.editPreco)).perform(replaceText("3,50"), closeSoftKeyboard());
            onView(withId(R.id.btnSalvar)).perform(scrollTo(), click());

            // ---------- R: o produto novo aparece na lista com o preço certo ----------
            onView(withText(nome)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.txtPrecoProduto), hasSibling(withText(nome))))
                    .check(matches(withText(Formatador.emReais(3.50))));

            // ---------- U: editar pelo botão "Editar" do produto ----------
            onView(botaoEditarDo(nome)).perform(scrollTo(), click());
            // A tela de edição abre com os dados atuais preenchidos
            onView(withId(R.id.editNome)).check(matches(withText(nome)));
            onView(withId(R.id.editNome)).perform(replaceText(nomeEditado), closeSoftKeyboard());
            onView(withId(R.id.editPreco)).perform(replaceText("4,75"), closeSoftKeyboard());
            onView(withId(R.id.btnSalvar)).perform(scrollTo(), click());

            // A lista mostra o nome e o preço novos, e o nome antigo sumiu
            onView(withText(nomeEditado)).perform(scrollTo()).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.txtPrecoProduto), hasSibling(withText(nomeEditado))))
                    .check(matches(withText(Formatador.emReais(4.75))));
            onView(withText(nome)).check(doesNotExist());

            // ---------- D: excluir pelo botão "Excluir produto" + confirmação ----------
            onView(botaoEditarDo(nomeEditado)).perform(scrollTo(), click());
            onView(withId(R.id.btnExcluir)).perform(scrollTo(), click());
            // Toca em "Sim" no AlertDialog de confirmação
            onView(withText(R.string.botao_sim)).inRoot(isDialog()).perform(click());

            // O produto não está mais na lista
            onView(withText(nomeEditado)).check(doesNotExist());
        }
    }

    /**
     * Encontra o botão "Editar" que está no mesmo cartão do produto com este nome.
     * (Todos os botões Editar têm o mesmo id, por isso usamos o nome como referência.)
     */
    private Matcher<View> botaoEditarDo(String nomeProduto) {
        return allOf(withId(R.id.btnEditar), hasSibling(hasDescendant(withText(nomeProduto))));
    }
}
