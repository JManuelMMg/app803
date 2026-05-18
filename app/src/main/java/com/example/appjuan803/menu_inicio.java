package com.example.appjuan803;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appjuan803.CamaraActivity;
import com.example.appjuan803.UbicacionActivity;

public class menu_inicio extends AppCompatActivity {

    private CardView btn_calculadora, btn_agenda, btn_ubicacion, btn_camara,
            btn_reproductor, btn_robot, btn_ccasa, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_inicio);

        btn_calculadora = findViewById(R.id.btn_calculadora);
        btn_agenda      = findViewById(R.id.btn_agenda);
        btn_ubicacion   = findViewById(R.id.btn_ubicacion);
        btn_camara      = findViewById(R.id.btn_camara);
        btn_reproductor = findViewById(R.id.btn_reproductor);
        btn_robot       = findViewById(R.id.btn_robot);
        btn_ccasa       = findViewById(R.id.btn_ccasa);
        btn_exit        = findViewById(R.id.btn_exit);

        btn_exit.setOnClickListener(v -> mostrarConfirmacionSalida());

        btn_calculadora.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, Calculadora.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Calculadora: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_agenda.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, MiProyecto.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Agenda: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // --- Nuevos Listeners ---
        btn_ccasa.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, CasaInteligenteActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Casa Inteligente: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_reproductor.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, ReproductorMusicaActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Reproductor: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_robot.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, RobotMenuActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Robot: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_ubicacion.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, UbicacionActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Ubicación: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btn_camara.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, CamaraActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo abrir Cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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