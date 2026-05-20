package com.example.appjuan803;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appjuan803.CamaraActivity;
import com.example.appjuan803.UbicacionActivity;

public class menu_inicio extends AppCompatActivity {

    private static final String TAG = "MenuInicio";
    private CardView btn_calculadora, btn_agenda, btn_ubicacion, btn_camara,
            btn_reproductor, btn_robot, btn_ccasa, btn_exit;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "=== Iniciando menu_inicio ===");

        try {
            EdgeToEdge.enable(this);
            Log.i(TAG, "EdgeToEdge habilitado");

            setContentView(R.layout.activity_menu_inicio);
            Log.i(TAG, "Layout cargado exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "ERROR CRÍTICO al cargar layout", e);
            e.printStackTrace();
            Toast.makeText(this, "Error fatal al cargar menú: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar botones con manejo seguro
        try {
            initializeButtons();
            Log.i(TAG, "Botones inicializados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar botones", e);
            e.printStackTrace();
            Toast.makeText(this, "Error al inicializar elementos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.i(TAG, "=== menu_inicio cargado completamente ===");
    }

    private void initializeButtons() {
        Log.i(TAG, "Inicializando referencias de botones...");

        btn_calculadora = findViewById(R.id.btn_calculadora);
        btn_agenda      = findViewById(R.id.btn_agenda);
        btn_ubicacion   = findViewById(R.id.btn_ubicacion);
        btn_camara      = findViewById(R.id.btn_camara);
        btn_reproductor = findViewById(R.id.btn_reproductor);
        btn_robot       = findViewById(R.id.btn_robot);
        btn_ccasa       = findViewById(R.id.btn_ccasa);
        btn_exit        = findViewById(R.id.btn_exit);

        // Validar que todos los botones fueron encontrados
        if (btn_calculadora == null) throw new RuntimeException("btn_calculadora no encontrado");
        if (btn_agenda == null) throw new RuntimeException("btn_agenda no encontrado");
        if (btn_ubicacion == null) throw new RuntimeException("btn_ubicacion no encontrado");
        if (btn_camara == null) throw new RuntimeException("btn_camara no encontrado");
        if (btn_reproductor == null) throw new RuntimeException("btn_reproductor no encontrado");
        if (btn_robot == null) throw new RuntimeException("btn_robot no encontrado");
        if (btn_ccasa == null) throw new RuntimeException("btn_ccasa no encontrado");
        if (btn_exit == null) throw new RuntimeException("btn_exit no encontrado");

        Log.i(TAG, "Todos los botones encontrados. Configurando listeners...");

        btn_exit.setOnClickListener(v -> mostrarConfirmacionSalida());

        // Calculadora
        btn_calculadora.setOnClickListener(v -> navegarAActividad("Calculadora", Calculadora.class));

        // Agenda
        btn_agenda.setOnClickListener(v -> navegarAActividad("Agenda", MiProyecto.class));

        // Casa Inteligente
        btn_ccasa.setOnClickListener(v -> navegarAActividad("Casa Inteligente", CasaInteligenteActivity.class));

        // Reproductor
        btn_reproductor.setOnClickListener(v -> navegarAActividad("Reproductor", ReproductorMusicaActivity.class));

        // Robot
        btn_robot.setOnClickListener(v -> navegarAActividad("Robot", RobotMenuActivity.class));

        // Ubicación
        btn_ubicacion.setOnClickListener(v -> navegarAActividad("Ubicación", UbicacionActivity.class));

        // Cámara
        btn_camara.setOnClickListener(v -> navegarAActividad("Cámara", CamaraActivity.class));

        Log.i(TAG, "Todos los listeners configurados");
    }

    private void navegarAActividad(String nombreActividad, Class<?> activityClass) {
        try {
            Log.i(TAG, "Abriendo: " + nombreActividad);
            startActivity(new Intent(menu_inicio.this, activityClass));
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir " + nombreActividad, e);
            Toast.makeText(menu_inicio.this,
                "No se pudo abrir " + nombreActividad + ": " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarConfirmacionSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                .setIcon(R.drawable.cerrar)
                .setCancelable(false)
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    // Solo cerrar actividades; evitar System.exit para no forzar cierre abrupto del proceso
                    finishAffinity();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}