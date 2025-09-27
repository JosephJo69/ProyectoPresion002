package com.example.proyectopresion001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectopresion001.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText sistolicaEditText, diastolicaEditText, edadEditText;
    private TextView sistolicaRequired;
    private Button consultarButton, historialButton;
    private DatabaseHelper dbHelper;
    private static final String GEMINI_API_KEY = "API";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        sistolicaEditText = findViewById(R.id.sistolicaEditText);
        diastolicaEditText = findViewById(R.id.diastolicaEditText);
        edadEditText = findViewById(R.id.edadEditText);
        sistolicaRequired = findViewById(R.id.sistolicaRequired);
        consultarButton = findViewById(R.id.consultarButton);
        historialButton = findViewById(R.id.historialButton);
    }

    private void setupListeners() {
        consultarButton.setOnClickListener(v -> validarYConsultar());
        historialButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });
    }

    private void validarYConsultar() {
        String sistolicaStr = sistolicaEditText.getText().toString();
        if (sistolicaStr.isEmpty()) {
            sistolicaRequired.setVisibility(View.VISIBLE);
            return;
        }
        sistolicaRequired.setVisibility(View.GONE);

        int sistolica = Integer.parseInt(sistolicaStr);
        Integer diastolica = null;
        Integer edad = null;

        String diastolicaStr = diastolicaEditText.getText().toString();
        if (!diastolicaStr.isEmpty()) {
            diastolica = Integer.parseInt(diastolicaStr);
        }

        String edadStr = edadEditText.getText().toString();
        if (!edadStr.isEmpty()) {
            edad = Integer.parseInt(edadStr);
        }

        // Guardar en la base de datos
        dbHelper.insertRegistro(sistolica, diastolica, edad);

        // Consultar a Gemini
        consultarGemini(sistolica, diastolica, edad);
    }

    private void consultarGemini(int sistolica, Integer diastolica, Integer edad) {
        String prompt = String.format(
            "Analiza los siguientes datos de presión arterial y proporciona una explicación corta y recomendaciones:\n" +
            "Presión Sistólica: %d mm Hg\n" +
            "Presión Diastólica: %s mm Hg\n" +
            "Edad: %s años\n" +
            "¿La presión es alta o baja? Da una breve recomendación.",
            sistolica,
            diastolica != null ? diastolica.toString() : "No proporcionada",
            edad != null ? edad.toString() : "No proporcionada"
        );

        try {
            JSONObject jsonBody = new JSONObject()
                .put("contents", new JSONArray()
                    .put(new JSONObject()
                        .put("parts", new JSONArray()
                            .put(new JSONObject()
                                .put("text", prompt)))));

            RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(GEMINI_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-goog-api-key", GEMINI_API_KEY)
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Error al consultar: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String resultado = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                        runOnUiThread(() -> mostrarResultado(resultado));
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "Error al procesar la respuesta: " + e.getMessage(),
                            Toast.LENGTH_LONG).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this,
                "Error al preparar la consulta: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarResultado(String resultado) {
        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra(ResultadoActivity.EXTRA_RESULTADO, resultado);
        startActivity(intent);
    }
}