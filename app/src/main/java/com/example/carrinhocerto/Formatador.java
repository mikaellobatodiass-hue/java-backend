package com.example.carrinhocerto;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Classe de apoio usada por todas as telas.
 * Guarda os nomes das informações enviadas pela Intent e
 * transforma números em valores no formato de reais (ex.: R$ 28,90).
 */
public class Formatador {

    // Nomes (chaves) usados para enviar e receber dados entre as telas pela Intent
    public static final String CHAVE_ORCAMENTO = "orcamento";
    public static final String CHAVE_TOTAL = "total";
    public static final String CHAVE_ID_PRODUTO = "id_produto";

    // Formatador de moeda no padrão brasileiro (vírgula nos centavos e símbolo R$)
    private static final NumberFormat FORMATO_REAL =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    /**
     * Recebe um número (ex.: 28.9) e devolve o texto "R$ 28,90".
     */
    public static String emReais(double valor) {
        return FORMATO_REAL.format(valor);
    }
}
