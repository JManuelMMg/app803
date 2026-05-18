package com.example.appjuan803;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RobotMenuActivity extends AppCompatActivity {

    // Declaración de CardViews
    private CardView cardPerfil, cardRoboFut, cardTres, cardControlRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_robot_menu);

        // Inicializar CardViews
        initializeCardViews();

        // Configurar listeners
        setupClickListeners();

        try {
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inicializar CardViews
    private void initializeCardViews() {
        cardPerfil = findViewById(R.id.cardPerfil);
        cardRoboFut = findViewById(R.id.cardRoboFut);
        cardTres = findViewById(R.id.cardTres);
        cardControlRobot = findViewById(R.id.cardControlRobot);
    }

    // Configurar listeners para los CardViews
    private void setupClickListeners() {
        // Perfil
        if (cardPerfil != null) {
            cardPerfil.setOnClickListener(v -> handlePerfil());
        }

        // RoboFut
        if (cardRoboFut != null) {
            cardRoboFut.setOnClickListener(v -> handleRoboFut());
        }

        // Robot Autónomo
        if (cardTres != null) {
            cardTres.setOnClickListener(v -> handleRobotAutonomo());
        }

        // Control Robot
        if (cardControlRobot != null) {
            cardControlRobot.setOnClickListener(v -> handleControlRobot());
        }
    }

    // Manejador para Perfil
    private void handlePerfil() {
        Toast.makeText(this, "Perfil seleccionado", Toast.LENGTH_SHORT).show();
        // TODO: Navegar a PuertaActivity o crear ProfileActivity
        try {
            Intent intent = new Intent(this, PuertaActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Manejador para RoboFut
    private void handleRoboFut() {
        Toast.makeText(this, "RoboFut - Control activado", Toast.LENGTH_SHORT).show();
        // TODO: Navegar a RobotControlActivity cuando esté creada
        navigateToRobotController();
    }

    // Manejador para Robot Autónomo
    private void handleRobotAutonomo() {
        Toast.makeText(this, "Robot Autónomo - Modo iniciado", Toast.LENGTH_SHORT).show();
        // TODO: Implementar navegación a Robot Autónomo
        navigateToRobotController();
    }

    // Manejador para Control Robot
    private void handleControlRobot() {
        Toast.makeText(this, "Controlador del Robot", Toast.LENGTH_SHORT).show();
        navigateToRobotController();
    }

    // Método para navegar al controlador del robot
    private void navigateToRobotController() {
        try {
            // Intentar navegar a RobotControlActivity si existe
            Intent intent = new Intent(this, Class.forName("com.example.appjuan803.RobotControlActivity"));
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            // Si no existe, mostrar mensaje
            Toast.makeText(this, "Activity no implementada aún", Toast.LENGTH_SHORT).show();
        }
    }
}