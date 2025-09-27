package com.example.proyectopresion001;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectopresion001.db.DatabaseHelper;
import com.example.proyectopresion001.db.Registro;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.registrosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Registro> registros = dbHelper.getAllRegistros();
        RegistrosAdapter adapter = new RegistrosAdapter(registros);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.volverButton).setOnClickListener(v -> finish());
    }

    private class RegistrosAdapter extends RecyclerView.Adapter<RegistroViewHolder> {
        private List<Registro> registros;

        RegistrosAdapter(List<Registro> registros) {
            this.registros = registros;
        }

        @Override
        public RegistroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            return new RegistroViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(RegistroViewHolder holder, int position) {
            Registro registro = registros.get(position);
            String texto = String.format("Fecha: %s\nSistólica: %d\nDiastólica: %d\nEdad: %d\n",
                    registro.getFecha(),
                    registro.getSistolica(),
                    registro.getDiastolica(),
                    registro.getEdad());
            holder.textView.setText(texto);
        }

        @Override
        public int getItemCount() {
            return registros.size();
        }
    }

    private static class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        RegistroViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}
