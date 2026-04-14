package com.example.appjuan803;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    EditText txtNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Castear btn xml a código
        btnStart = findViewById(R.id.btnStart);
        txtNombre = findViewById(R.id.txtNombre);

        // Evento btn
        btnStart.setOnClickListener(v -> {
            // Recibir el texto de la caja y almacenarlo
            String nombre = txtNombre.getText().toString();
            imprimirmensaje("Hola " + nombre.toUpperCase());
        });
    }

    // 🔹 Método para mostrar mensaje (Toast)
    private void imprimirmensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}