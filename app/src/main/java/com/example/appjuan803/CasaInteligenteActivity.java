package com.example.appjuan803;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** Actividad para controlar dispositivos inteligentes del hogar */
public class CasaInteligenteActivity extends AppCompatActivity {

    // Declaración de componentes
    private Button btnLuzSala, btnLuzCocina, btnLuzDormitorio;
    private Button btnReducirTemp, btnAumentarTemp;
    private Button btnBloquearPuertas, btnActivarAlarma, btnCamaras;
    private Button btnTV, btnMusica;
    private Button btnActualizarEstado;
    private TextView txtTemperatura, txtEstado;

    // Estados de dispositivos
    private boolean[] luces = {false, false, false}; // Sala, Cocina, Dormitorio
    private int temperatura = 22;
    private boolean puertasBlockeadas = false;
    private boolean alarmaActivada = false;
    private boolean tvEncendido = false;
    private boolean musicaEncendida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa_inteligente);

        // Inicializar componentes
        initializeComponents();

        // Configurar listeners
        setupClickListeners();

        // Mostrar estado inicial
        updateSystemStatus();
    }

    // Inicializar todos los componentes del layout
    private void initializeComponents() {
        // Luces
        btnLuzSala = findViewById(R.id.btnLuzSala);
        btnLuzCocina = findViewById(R.id.btnLuzCocina);
        btnLuzDormitorio = findViewById(R.id.btnLuzDormitorio);

        // Temperatura
        btnReducirTemp = findViewById(R.id.btnReducirTemp);
        btnAumentarTemp = findViewById(R.id.btnAumentarTemp);
        txtTemperatura = findViewById(R.id.txtTemperatura);

        // Seguridad
        btnBloquearPuertas = findViewById(R.id.btnBloquearPuertas);
        btnActivarAlarma = findViewById(R.id.btnActivarAlarma);
        btnCamaras = findViewById(R.id.btnCamaras);

        // Entretenimiento
        btnTV = findViewById(R.id.btnTV);
        btnMusica = findViewById(R.id.btnMusica);

        // Estado
        btnActualizarEstado = findViewById(R.id.btnActualizarEstado);
        txtEstado = findViewById(R.id.txtEstado);
    }

    // Configurar todos los listeners
    private void setupClickListeners() {
        // Luces
        btnLuzSala.setOnClickListener(v -> toggleLuz(0, "Sala"));
        btnLuzCocina.setOnClickListener(v -> toggleLuz(1, "Cocina"));
        btnLuzDormitorio.setOnClickListener(v -> toggleLuz(2, "Dormitorio"));

        // Temperatura
        btnReducirTemp.setOnClickListener(v -> adjustTemperature(-1));
        btnAumentarTemp.setOnClickListener(v -> adjustTemperature(1));

        // Seguridad
        btnBloquearPuertas.setOnClickListener(v -> togglePuertas());
        btnActivarAlarma.setOnClickListener(v -> toggleAlarma());
        btnCamaras.setOnClickListener(v -> toggleCamaras());

        // Entretenimiento
        btnTV.setOnClickListener(v -> toggleTV());
        btnMusica.setOnClickListener(v -> toggleMusica());

        // Estado
        btnActualizarEstado.setOnClickListener(v -> updateSystemStatus());
    }

    // Alternar estado de luces
    private void toggleLuz(int index, String room) {
        luces[index] = !luces[index];
        String estado = luces[index] ? "encendida" : "apagada";
        Toast.makeText(this, "💡 Luz de " + room + " " + estado, Toast.LENGTH_SHORT).show();
        updateButtonStatus(index);
    }

    // Actualizar estado visual del botón
    private void updateButtonStatus(int index) {
        Button btn = null;
        String prefix = "🔆 ";
        if (index == 0) {
            btn = btnLuzSala;
            prefix = luces[0] ? "🔆 " : "";
        } else if (index == 1) {
            btn = btnLuzCocina;
            prefix = luces[1] ? "🔆 " : "";
        } else if (index == 2) {
            btn = btnLuzDormitorio;
            prefix = luces[2] ? "🔆 " : "";
        }

        if (btn != null) {
            String originalText = btn.getText().toString();
            // Remover prefijo anterior si existe
            if (originalText.startsWith("🔆")) {
                originalText = originalText.substring(2).trim();
            }
            btn.setText(prefix + originalText);
        }
    }

    // Ajustar temperatura
    private void adjustTemperature(int delta) {
        temperatura += delta;

        // Limitar rango de temperatura
        if (temperatura < 16) {
            temperatura = 16;
            Toast.makeText(this, "❄️ Temperatura mínima alcanzada (16°C)", Toast.LENGTH_SHORT).show();
        } else if (temperatura > 30) {
            temperatura = 30;
            Toast.makeText(this, "🔥 Temperatura máxima alcanzada (30°C)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "🌡️ Temperatura: " + temperatura + "°C", Toast.LENGTH_SHORT).show();
        }

        txtTemperatura.setText(temperatura + "°C");
    }

    // Alternar bloqueo de puertas
    private void togglePuertas() {
        puertasBlockeadas = !puertasBlockeadas;
        String estado = puertasBlockeadas ? "🔒 Bloqueadas" : "🔓 Desbloqueadas";
        Toast.makeText(this, "🚪 Puertas " + estado, Toast.LENGTH_SHORT).show();
        btnBloquearPuertas.setText(estado);
    }

    // Alternar alarma
    private void toggleAlarma() {
        alarmaActivada = !alarmaActivada;
        String estado = alarmaActivada ? "🚨 ACTIVADA" : "🔕 Desactivada";
        Toast.makeText(this, "Alarma " + estado, Toast.LENGTH_SHORT).show();
        btnActivarAlarma.setText(estado);
    }

    // Alternar cámaras
    private void toggleCamaras() {
        Toast.makeText(this, "📹 Sistema de cámaras consultando...", Toast.LENGTH_SHORT).show();
    }

    // Alternar TV
    private void toggleTV() {
        tvEncendido = !tvEncendido;
        String estado = tvEncendido ? "📺 ENCENDIDO" : "📺 Apagado";
        Toast.makeText(this, "Televisor " + estado, Toast.LENGTH_SHORT).show();
        btnTV.setText(estado);
    }

    // Alternar música
    private void toggleMusica() {
        musicaEncendida = !musicaEncendida;
        String estado = musicaEncendida ? "🎵 Reproduciendo" : "🎵 Detenida";
        Toast.makeText(this, "Música " + estado, Toast.LENGTH_SHORT).show();
        btnMusica.setText(estado);
    }

    // Actualizar estado del sistema
    private void updateSystemStatus() {
        // Contar dispositivos activos
        int activos = 0;
        if (luces[0]) activos++;
        if (luces[1]) activos++;
        if (luces[2]) activos++;
        if (tvEncendido) activos++;
        if (musicaEncendida) activos++;

        String status = "";
        if (activos == 0 && !alarmaActivada && !puertasBlockeadas) {
            status = "✅ Casa en modo ahorro de energía";
            txtEstado.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (alarmaActivada) {
            status = "🔴 ¡ALARMA ACTIVADA! Puertas bloqueadas";
            txtEstado.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            status = "✅ Sistema funcionando correctamente (" + activos + " dispositivos activos)";
            txtEstado.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }

        txtEstado.setText(status);
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }
}
