package com.example.appjuan803;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu_inicio extends AppCompatActivity {

    // Declaración de variables para los botones
    private CardView btnSaldo, btnGastos, btnPerfil, btnReportar, btnBuscar, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_inicio);

        // 🔹 1. Referenciar los botones del XML
        inicializarVistas();

        // 🔹 2. Configurar los eventos click
        configurarClicks();
    }

    /**
     * Método para inicializar las vistas (findViewById)
     */
    private void inicializarVistas() {
        btnSaldo = findViewById(R.id.btn_saldo);
        btnGastos = findViewById(R.id.btn_gastos);
        btnPerfil = findViewById(R.id.btn_perfil);
        btnReportar = findViewById(R.id.btn_reportar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnExit = findViewById(R.id.btn_exit);
    }

    /**
     * Método para configurar los listeners de cada botón
     */
    private void configurarClicks() {

        // 💰 Botón: Saldo (ACCIÓN VACÍA - Pendiente de programar)
        btnSaldo.setOnClickListener(v -> {
            // TODO: Implementar lógica para Saldo
            Toast.makeText(this, "Saldo: Pendiente", Toast.LENGTH_SHORT).show();
        });

        // 💸 Botón: Gastos (ACCIÓN VACÍA - Pendiente de programar)
        btnGastos.setOnClickListener(v -> {
            // TODO: Implementar lógica para Gastos
            Toast.makeText(this, "Gastos: Pendiente", Toast.LENGTH_SHORT).show();
        });

        // 👤 Botón: Perfil (ACCIÓN VACÍA - Pendiente de programar)
        btnPerfil.setOnClickListener(v -> {
            // TODO: Implementar lógica para Perfil
            Toast.makeText(this, "Perfil: Pendiente", Toast.LENGTH_SHORT).show();
        });

        // 📢 Botón: Reportar (ACCIÓN VACÍA - Pendiente de programar)
        btnReportar.setOnClickListener(v -> {
            // TODO: Implementar lógica para Reportar
            Toast.makeText(this, "Reportar: Pendiente", Toast.LENGTH_SHORT).show();
        });

        // 🔍 Botón: Buscar (ACCIÓN VACÍA - Pendiente de programar)
        btnBuscar.setOnClickListener(v -> {
            // TODO: Implementar lógica para Buscar
            Toast.makeText(this, "Buscar: Pendiente", Toast.LENGTH_SHORT).show();
        });

        // 🚪 Botón: Exit (✅ PROGRAMADO - Con confirmación)
        btnExit.setOnClickListener(v -> {
            mostrarConfirmacionSalida();
        });
    }

    /**
     * ✅ Método para mostrar diálogo de confirmación al salir
     */
    private void mostrarConfirmacionSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                .setIcon(R.drawable.ic_exit)
                .setCancelable(false)
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    // Cerrar aplicación completamente
                    finishAffinity();  // Cierra todas las actividades de la pila
                    System.exit(0);    // Finaliza el proceso
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();  // Solo cierra el diálogo
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpieza opcional de referencias
        btnSaldo = null;
        btnGastos = null;
        btnPerfil = null;
        btnReportar = null;
        btnBuscar = null;
        btnExit = null;
    }
}