package com.example.proyectopresion001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectopresion001.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private static final String GEMINI_API_KEY = "AIzaSyCmjIQK4RoPzvjxxepC3hBiEgTLaMkIe1k";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    // Webhook de n8n proporcionado por el usuario
    private static final String N8N_WEBHOOK_URL = "https://jojo001.app.n8n.cloud/webhook-test/b8ee7483-2ce4-419a-abe8-ee09ad48c14e";
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
        CharSequence csSist = sistolicaEditText.getText();
        String sistolicaStr = csSist == null ? "" : csSist.toString().trim();
        if (sistolicaStr.isEmpty()) {
            sistolicaRequired.setVisibility(View.VISIBLE);
            return;
        }
        sistolicaRequired.setVisibility(View.GONE);

        int sistolica = Integer.parseInt(sistolicaStr);
        Integer diastolica = null;
        Integer edad = null;

        CharSequence csDia = diastolicaEditText.getText();
        String diastolicaStr = csDia == null ? "" : csDia.toString().trim();
        if (!diastolicaStr.isEmpty()) {
            diastolica = Integer.parseInt(diastolicaStr);
        }

        CharSequence csEdad = edadEditText.getText();
        String edadStr = csEdad == null ? "" : csEdad.toString().trim();
        if (!edadStr.isEmpty()) {
            edad = Integer.parseInt(edadStr);
        }

        // Guardar en la base de datos
        dbHelper.insertRegistro(sistolica, diastolica, edad);

        // Enviar al webhook de n8n de forma asíncrona
        String fecha = getCurrentIsoTimestamp();
        sendToN8nWebhook(sistolica, diastolica, edad, fecha);

        // Consultar a Gemini
        consultarGemini(sistolica, diastolica, edad);
    }

    // Devuelve la fecha actual en formato ISO 8601 UTC
    private String getCurrentIsoTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    // Envía los datos al webhook de n8n (append a Google Sheets se realiza en n8n)
    private void sendToN8nWebhook(int sistolica, Integer diastolica, Integer edad, String fecha) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("sistolica", sistolica);
            if (diastolica != null) payload.put("diastolica", diastolica); else payload.put("diastolica", JSONObject.NULL);
            if (edad != null) payload.put("edad", edad); else payload.put("edad", JSONObject.NULL);
            payload.put("fecha", fecha);

            RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(N8N_WEBHOOK_URL)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "Fallo al añadir datos al documento",
                            Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "Fallo al añadir datos al documento",
                                    Toast.LENGTH_LONG).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "Datos enviados al documento",
                                    Toast.LENGTH_LONG).show());
                        }
                    } finally {
                        if (response.body() != null) response.body().close();
                        response.close();
                    }
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "Fallo al añadir datos al documento",
                    Toast.LENGTH_LONG).show());
        }
    }

    private void consultarGemini(int sistolica, Integer diastolica, Integer edad) {
        String prompt = String.format(Locale.US,
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
                 public void onFailure(@NonNull Call call, @NonNull IOException e) {
                     runOnUiThread(() -> Toast.makeText(MainActivity.this,
                         "Error al consultar: " + e.getMessage(),
                         Toast.LENGTH_LONG).show());
                 }

                 @Override
                 public void onResponse(@NonNull Call call, @NonNull Response response) {
                     try {
                         okhttp3.ResponseBody responseBody = response.body();
                         if (responseBody == null) {
                             runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                 "Respuesta vacía del servidor",
                                 Toast.LENGTH_LONG).show());
                             return;
                         }
                         String responseData = responseBody.string();
                         responseBody.close();
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
