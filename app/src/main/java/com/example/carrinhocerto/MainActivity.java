package com.example.carrinhocerto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TELA 1 - Início.
 * O usuário digita o orçamento do mês e toca em "Começar".
 */
public class MainActivity extends AppCompatActivity {

    // Componentes da tela
    private EditText editOrcamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Liga esta classe ao layout activity_main.xml
        setContentView(R.layout.activity_main);

        // Busca os componentes do layout pelo id
        editOrcamento = findViewById(R.id.editOrcamento);
        Button btnComecar = findViewById(R.id.btnComecar);

        // O que acontece quando o botão "Começar" é tocado
        btnComecar.setOnClickListener(v -> comecar());
    }

    /**
     * Lê o orçamento digitado, valida e abre a tela da lista.
     */
    private void comecar() {
        // Pega o texto do campo e troca vírgula por ponto (ex.: "500,50" vira "500.50")
        String texto = editOrcamento.getText().toString().trim().replace(",", ".");

        double orcamento;
        try {
            // Converte o texto em número
            orcamento = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            // Se estiver vazio ou inválido, consideramos zero
            orcamento = 0;
        }

        // Se o valor for zero (ou vazio), avisa o usuário com um Toast e não continua
        if (orcamento <= 0) {
            Toast.makeText(this, R.string.aviso_orcamento, Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria a Intent para abrir a ListaActivity e envia o orçamento junto
        Intent intent = new Intent(this, ListaActivity.class);
        intent.putExtra(Formatador.CHAVE_ORCAMENTO, orcamento);
        startActivity(intent);
    }
}
