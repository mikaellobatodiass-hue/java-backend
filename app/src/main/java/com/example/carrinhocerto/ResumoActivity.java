package com.example.carrinhocerto;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * TELA 3 - Resumo.
 * Mostra orçamento, total, saldo e uma barra com quanto do orçamento foi usado.
 */
public class ResumoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo);

        // Recebe os valores enviados pela ListaActivity
        double orcamento = getIntent().getDoubleExtra(Formatador.CHAVE_ORCAMENTO, 0);
        double total = getIntent().getDoubleExtra(Formatador.CHAVE_TOTAL, 0);

        // Saldo = quanto sobra do orçamento (fica negativo se passar)
        double saldo = orcamento - total;

        // Busca os componentes do layout
        TextView txtOrcamento = findViewById(R.id.txtOrcamento);
        TextView txtTotal = findViewById(R.id.txtTotal);
        TextView txtSaldo = findViewById(R.id.txtSaldo);
        TextView txtPercentual = findViewById(R.id.txtPercentual);
        TextView txtMensagem = findViewById(R.id.txtMensagem);
        ProgressBar barraOrcamento = findViewById(R.id.barraOrcamento);
        Button btnVerDicas = findViewById(R.id.btnVerDicas);
        Button btnVoltarLista = findViewById(R.id.btnVoltarLista);

        // Mostra os valores em reais
        txtOrcamento.setText(Formatador.emReais(orcamento));
        txtTotal.setText(Formatador.emReais(total));
        txtSaldo.setText(Formatador.emReais(saldo));

        // Calcula a porcentagem do orçamento que foi usada
        int percentual = 0;
        if (orcamento > 0) {
            percentual = (int) Math.round(total / orcamento * 100);
        }
        txtPercentual.setText(getString(R.string.percentual_usado, percentual));

        // A barra vai de 0 a 100; se passar de 100% ela fica cheia
        barraOrcamento.setProgress(Math.min(percentual, 100));

        // Escolhe as cores e a mensagem conforme o resultado
        int cor;       // cor forte: barra, textos e saldo
        int corFundo;  // cor clarinha: fundo da caixa da mensagem
        if (total <= orcamento) {
            // Dentro do orçamento: verde
            cor = ContextCompat.getColor(this, R.color.verde_ok);
            corFundo = ContextCompat.getColor(this, R.color.verde_ok_claro);
            txtMensagem.setText(R.string.msg_dentro);
        } else {
            // Passou do orçamento: vermelho e mostra quanto passou
            cor = ContextCompat.getColor(this, R.color.vermelho_alerta);
            corFundo = ContextCompat.getColor(this, R.color.vermelho_claro);
            double excesso = total - orcamento;
            txtMensagem.setText(getString(R.string.msg_fora, Formatador.emReais(excesso)));
        }

        // Aplica as cores na barra, no percentual, na mensagem e no saldo
        barraOrcamento.setProgressTintList(ColorStateList.valueOf(cor));
        txtPercentual.setTextColor(cor);
        txtMensagem.setTextColor(cor);
        txtMensagem.setBackgroundTintList(ColorStateList.valueOf(corFundo));
        txtSaldo.setTextColor(cor);

        // Botão "Ver Dicas": abre a DicasActivity
        btnVerDicas.setOnClickListener(v ->
                startActivity(new Intent(this, DicasActivity.class)));

        // Botão "Voltar para a lista": fecha esta tela e volta para a lista
        // (os produtos marcados continuam marcados)
        btnVoltarLista.setOnClickListener(v -> finish());
    }
}
