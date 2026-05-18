package com.example.appjuan803;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class RobotControlActivity extends AppCompatActivity {

    // Declaración de componentes
    private MaterialButton btnConnect, btnManual, btnLed, btnF, btnB, btnL, btnR;
    private TextView statusBt;

    // Estados
    private boolean isConnected = false;
    private boolean ledOn = false;
    private boolean isMoving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_robot_controller);

        // Inicializar componentes
        initializeComponents();

        // Configurar listeners
        setupClickListeners();

        // Aplicar EdgeToEdge
        applyEdgeToEdge();
    }

    // Inicializar todos los componentes del layout
    private void initializeComponents() {
        btnConnect = findViewById(R.id.btnConnect);
        btnManual = findViewById(R.id.btnManual);
        btnLed = findViewById(R.id.btnLed);
        btnF = findViewById(R.id.btnF);
        btnB = findViewById(R.id.btnB);
        btnL = findViewById(R.id.btnL);
        btnR = findViewById(R.id.btnR);
        statusBt = findViewById(R.id.statusBt);
    }

    // Configurar todos los listeners
    private void setupClickListeners() {
        // Botón Conectar
        if (btnConnect != null) {
            btnConnect.setOnClickListener(v -> toggleConnection());
        }

        // Botón Modo Manual
        if (btnManual != null) {
            btnManual.setOnClickListener(v -> toggleManualMode());
        }

        // Botón LED
        if (btnLed != null) {
            btnLed.setOnClickListener(v -> toggleLED());
        }

        // Botones de movimiento
        if (btnF != null) {
            btnF.setOnClickListener(v -> moveForward());
            btnF.setOnTouchListener((v, event) -> handleButtonTouch(event, "ADELANTE"));
        }

        if (btnB != null) {
            btnB.setOnClickListener(v -> moveBackward());
            btnB.setOnTouchListener((v, event) -> handleButtonTouch(event, "ATRÁS"));
        }

        if (btnL != null) {
            btnL.setOnClickListener(v -> moveLeft());
            btnL.setOnTouchListener((v, event) -> handleButtonTouch(event, "IZQUIERDA"));
        }

        if (btnR != null) {
            btnR.setOnClickListener(v -> moveRight());
            btnR.setOnTouchListener((v, event) -> handleButtonTouch(event, "DERECHA"));
        }
    }

    // Manejo de conexión
    private void toggleConnection() {
        isConnected = !isConnected;
        if (isConnected) {
            statusBt.setText("● CONECTADO");
            statusBt.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            btnConnect.setText("DESCONECTAR");
            Toast.makeText(this, "✅ Robot conectado", Toast.LENGTH_SHORT).show();
        } else {
            statusBt.setText("● DESCONECTADO");
            statusBt.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            btnConnect.setText("CONECTAR");
            Toast.makeText(this, "❌ Robot desconectado", Toast.LENGTH_SHORT).show();
        }
    }

    // Modo manual
    private void toggleManualMode() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "🎮 Modo Manual activado", Toast.LENGTH_SHORT).show();
    }

    // Control de LED
    private void toggleLED() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        ledOn = !ledOn;
        if (ledOn) {
            btnLed.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            Toast.makeText(this, "💡 LED encendido", Toast.LENGTH_SHORT).show();
        } else {
            btnLed.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            Toast.makeText(this, "💡 LED apagado", Toast.LENGTH_SHORT).show();
        }
    }

    // Movimiento adelante
    private void moveForward() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "⬆️ Moviendo adelante...", Toast.LENGTH_SHORT).show();
    }

    // Movimiento atrás
    private void moveBackward() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "⬇️ Moviendo atrás...", Toast.LENGTH_SHORT).show();
    }

    // Movimiento izquierda
    private void moveLeft() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "⬅️ Girando izquierda...", Toast.LENGTH_SHORT).show();
    }

    // Movimiento derecha
    private void moveRight() {
        if (!isConnected) {
            Toast.makeText(this, "⚠️ Primero conecta el robot", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "➡️ Girando derecha...", Toast.LENGTH_SHORT).show();
    }

    // Manejo de eventos táctiles (presionar y soltar)
    private boolean handleButtonTouch(android.view.MotionEvent event, String direction) {
        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                // Botón presionado
                Toast.makeText(this, "🤖 " + direction, Toast.LENGTH_SHORT).show();
                break;
            case android.view.MotionEvent.ACTION_UP:
                // Botón soltado
                Toast.makeText(this, "🛑 Detenido", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    // Aplicar EdgeToEdge
    private void applyEdgeToEdge() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

