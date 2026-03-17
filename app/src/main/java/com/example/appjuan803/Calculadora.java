package com.example.appjuan803;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Importe necesario para la notificación

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Calculadora extends AppCompatActivity {

    // Variables de vista
    private TextInputEditText etA, etB, etC;
    private Button btnResolver;
    private TextView tvResultado;
    private TextView tvFormula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculadora);

        // 1. Vincular vistas
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        btnResolver = findViewById(R.id.btnResolver);
        tvResultado = findViewById(R.id.tvResultado);
        tvFormula = findViewById(R.id.tvFormula);

        // 2. Configurar TextWatcher para la fórmula dinámica
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarFormula();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        if (etA != null) etA.addTextChangedListener(watcher);
        if (etB != null) etB.addTextChangedListener(watcher);
        if (etC != null) etC.addTextChangedListener(watcher);

        // 3. Configurar botón Resolver
        btnResolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolverEcuacion();
            }
        });
    }

    // Lógica para actualizar la fórmula en tiempo real
    private void actualizarFormula() {
        String a = getSafeValue(etA);
        String b = getSafeValue(etB);
        String c = getSafeValue(etC);

        String parteA = a.isEmpty() ? "a" : a;

        String parteB;
        if (b.isEmpty()) {
            parteB = "b";
        } else {
            if (!b.startsWith("-")) {
                parteB = "+ " + b;
            } else {
                parteB = "- " + b.replace("-", "");
            }
        }

        String parteC;
        if (c.isEmpty()) {
            parteC = "c";
        } else {
            if (!c.startsWith("-")) {
                parteC = "+ " + c;
            } else {
                parteC = "- " + c.replace("-", "");
            }
        }

        tvFormula.setText(parteA + "x² " + parteB + "x " + parteC + " = 0");
    }

    private String getSafeValue(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) return "";
        String val = editText.getText().toString().trim();
        return val.isEmpty() ? "" : val;
    }

    // Lógica matemática con librería Math y Notificación Toast
    private void resolverEcuacion() {
        String strA = getSafeValue(etA);
        String strB = getSafeValue(etB);
        String strC = getSafeValue(etC);

        if (strA.isEmpty() || strB.isEmpty() || strC.isEmpty()) {
            tvResultado.setText("⚠️ Ingresa todos los coeficientes");
            tvResultado.setTextColor(0xFFFFAB40);

            return;
        }

        double a = Double.parseDouble(strA);
        double b = Double.parseDouble(strB);
        double c = Double.parseDouble(strC);

        if (a == 0) {
            tvResultado.setText("⚠️ El coeficiente 'a' no puede ser 0");
            tvResultado.setTextColor(0xFFFF5252);
            return;
        }

        double discriminante = (b * b) - (4 * a * c);

        if (discriminante > 0) {
            double x1 = (-b + Math.sqrt(discriminante)) / (2 * a);
            double x2 = (-b - Math.sqrt(discriminante)) / (2 * a);
            tvResultado.setText(String.format("Solucion Real:\nx₁ = %.2f\nx₂ = %.2f", x1, x2));
            tvResultado.setTextColor(0xFFFFFFFF);

            // NOTIFICACIÓN DE ÉXITO
            Toast.makeText(this, "2 soluciones", Toast.LENGTH_SHORT).show();

        } else if (discriminante == 0) {
            double x = -b / (2 * a);
            tvResultado.setText(String.format("Única solución:\nx = %.2f", x));
            tvResultado.setTextColor(0xFFFFFFFF);

            // NOTIFICACIÓN DE ÉXITO
            Toast.makeText(this, "Ecuación de solución)", Toast.LENGTH_SHORT).show();

        } else {
            double real = -b / (2 * a);
            double imag = Math.sqrt(Math.abs(discriminante)) / (2 * a);
            tvResultado.setText(String.format("solucion Complejas:\nx₁ = %.2f + %.2fi\nx₂ = %.2f - %.2fi", real, imag, real, imag));
            tvResultado.setTextColor(0xFF4FC3F7);

            // NOTIFICACIÓN DE ÉXITO
            Toast.makeText(this, "Ecuación resuelta (Complejas)", Toast.LENGTH_SHORT).show();
        }
    }
}