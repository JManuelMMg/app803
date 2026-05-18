package com.example.appjuan803;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PuertaActivity extends AppCompatActivity implements SensorEventListener {

    TextView btnRegresar, txtEstadoPuerta, txtSensorTexto;
    ImageView iconPuertaGrande;
    LinearLayout sensorContainer;

    Button btnAbrirPuerta, btnCerrarPuerta;

    SensorManager sensorManager;
    Sensor proximitySensor;

    private final String ESP_IP = "http://192.168.4.1";
    OkHttpClient client = new OkHttpClient();

    boolean puertaAbierta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puerta);

        btnRegresar = findViewById(R.id.btnRegresar);
        txtEstadoPuerta = findViewById(R.id.txtEstadoPuerta);
        iconPuertaGrande = findViewById(R.id.iconPuertaGrande);

        sensorContainer = findViewById(R.id.txtSensorInfo);
        txtSensorTexto = findViewById(R.id.txtSensorTexto);

        btnAbrirPuerta = findViewById(R.id.btnAbrirPuerta);
        btnCerrarPuerta = findViewById(R.id.btnCerrarPuerta);

        cerrarPuerta(false);

        btnRegresar.setOnClickListener(v -> finish());
        btnAbrirPuerta.setOnClickListener(v -> abrirPuerta(true));
        btnCerrarPuerta.setOnClickListener(v -> cerrarPuerta(true));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (proximitySensor != null) {
                txtSensorTexto.setText("Sensor de proximidad activo");
            } else {
                txtSensorTexto.setText("Este teléfono no tiene sensor de proximidad");
            }
        }
    }

    private void abrirPuerta(boolean enviarAlEsp) {
        puertaAbierta = true;

        txtEstadoPuerta.setText("Estado: Abierta");
        txtEstadoPuerta.setTextColor(Color.parseColor("#70E35E"));

        iconPuertaGrande.setAlpha(1f);
        iconPuertaGrande.animate()
                .rotationY(15f)
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(300)
                .start();

        txtSensorTexto.setText("Puerta abierta");

        if (enviarAlEsp) {
            enviarComando("/puerta/abrir");
        }
    }

    private void cerrarPuerta(boolean enviarAlEsp) {
        puertaAbierta = false;

        txtEstadoPuerta.setText("Estado: Cerrada");
        txtEstadoPuerta.setTextColor(Color.parseColor("#F59E0B"));

        iconPuertaGrande.setAlpha(0.85f);
        iconPuertaGrande.animate()
                .rotationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();

        txtSensorTexto.setText("Puerta cerrada");

        if (enviarAlEsp) {
            enviarComando("/puerta/cerrar");
        }
    }

    private void enviarComando(String endpoint) {
        String url = ESP_IP + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null && proximitySensor != null) {
            sensorManager.registerListener(
                    this,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (proximitySensor == null) return;

        float distancia = event.values[0];

        if (distancia < proximitySensor.getMaximumRange()) {
            if (!puertaAbierta) {
                abrirPuerta(true);
            }
            txtSensorTexto.setText("Objeto detectado → puerta abierta");
        } else {
            if (puertaAbierta) {
                cerrarPuerta(true);
            }
            txtSensorTexto.setText("Sin objeto → puerta cerrada");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita por ahora
    }
}