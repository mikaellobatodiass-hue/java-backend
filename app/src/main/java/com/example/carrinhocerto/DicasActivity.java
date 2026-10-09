package com.example.carrinhocerto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TELA 4 - Dicas e Sobre.
 * Mostra dicas de economia e os integrantes do grupo (textos no layout/strings.xml).
 */
public class DicasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicas);

        Button btnVoltarInicio = findViewById(R.id.btnVoltarInicio);

        // Botão "Voltar ao início": volta para a MainActivity
        btnVoltarInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            // CLEAR_TOP fecha todas as telas que estão por cima da MainActivity
            // (Lista, Resumo e Dicas), assim o botão "voltar" do celular não volta para elas
            // NEW_TASK + CLEAR_TASK garante uma tela inicial limpa, com o campo vazio
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
