package com.example.proyectopresion001;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultadoActivity extends AppCompatActivity {
    public static final String EXTRA_RESULTADO = "resultado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        String resultado = getIntent().getStringExtra(EXTRA_RESULTADO);
        TextView resultadoTextView = findViewById(R.id.resultadoTextView);
        resultadoTextView.setText(resultado);

        findViewById(R.id.volverButton).setOnClickListener(v -> finish());
    }
}
