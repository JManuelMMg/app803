package com.example.appjuan803;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Controles extends AppCompatActivity {

    // 🔹 Declaración de controles
    SeekBar sbNivel;
    CheckBox cbAceptar;
    RadioGroup rgSexo;
    ToggleButton tbInterruptor;
    CalendarView cvFecha;
    ProgressBar pbCarga;
    ImageView imFoto;
    RatingBar rbCalificacion;
    Button btnEnvio;

    // 🔹 Variables para control de progreso
    boolean nivelCompleto = false;
    boolean aceptarCompleto = false;
    boolean sexoCompleto = false;
    boolean estadoCompleto = false;
    boolean fechaCompleto = false;
    boolean calificacionCompleto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controles);

        // 🔹 Inicialización de controles
        sbNivel = findViewById(R.id.sbNivel);
        cbAceptar = findViewById(R.id.abAceptar);
        rgSexo = findViewById(R.id.rbSexo);
        tbInterruptor = findViewById(R.id.tbInterruptor);
        cvFecha = findViewById(R.id.cvFecha);
        pbCarga = findViewById(R.id.pbCarga);
        imFoto = findViewById(R.id.imFoto);
        rbCalificacion = findViewById(R.id.rbCalificacion);
        btnEnvio = findViewById(R.id.btnEnvio);

        // 🔹 EVENTO: SeekBar - Nivel
        sbNivel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                actualizarProgreso(); // Actualiza barra sin mensaje
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ✅ Mensaje al soltar + actualizar progreso
                imprimirMensaje("📊 Nivel: " + seekBar.getProgress() + "%");
                nivelCompleto = true;
                actualizarProgreso();
            }
        });

        // 🔹 EVENTO: CheckBox - Aceptar ✅ Mensaje en cada cambio
        cbAceptar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            imprimirMensaje("☑ Aceptar: " + (isChecked ? "Sí" : "No"));
            aceptarCompleto = isChecked;
            actualizarProgreso();
        });

        // 🔹 EVENTO: RadioGroup - Sexo ✅ Mensaje en cada selección
        rgSexo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbM) {
                imprimirMensaje("👤 Sexo: Masculino");
            } else if (checkedId == R.id.rbF) {
                imprimirMensaje("👤 Sexo: Femenino");
            }
            sexoCompleto = true;
            actualizarProgreso();
        });

        // 🔹 EVENTO: ToggleButton - Estado ✅ Mensaje en cada cambio
        tbInterruptor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            imprimirMensaje("⚡ Estado: " + (isChecked ? "ACTIVO" : "INACTIVO"));
            estadoCompleto = true;
            actualizarProgreso();
        });

        // 🔹 EVENTO: CalendarView - Fecha ✅ Mensaje al seleccionar
        cvFecha.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
            imprimirMensaje("📅 Fecha: " + fecha);
            fechaCompleto = true;
            actualizarProgreso();
        });

        // 🔹 EVENTO: RatingBar - Calificación ✅ Mensaje al calificar
        rbCalificacion.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            imprimirMensaje("⭐ Calificación: " + rating + "/10");
            calificacionCompleto = (rating > 0);
            actualizarProgreso();
        });

        // 🔹 EVENTO: Button - Enviar → Diálogo CENTRADO
        btnEnvio.setOnClickListener(v -> {
            mostrarResumenCentrado();
        });

        // 🔹 Inicializar progreso base
        actualizarProgreso();
    }

    // 🔹 Calcula y actualiza ProgressBar según campos completados
    private void actualizarProgreso() {
        int completados = 0;
        if (nivelCompleto) completados++;
        if (aceptarCompleto) completados++;
        if (sexoCompleto) completados++;
        if (estadoCompleto) completados++;
        if (fechaCompleto) completados++;
        if (calificacionCompleto) completados++;

        int porcentaje = (completados * 100) / 6;
        pbCarga.setProgress(porcentaje);
    }

    // 🔹 Muestra diálogo CENTRADO con resumen de datos
    private void mostrarResumenCentrado() {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);
        layout.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

        TextView titulo = new TextView(this);
        titulo.setText("📋 Resumen de Datos");
        titulo.setTextSize(20);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(0, 0, 0, 20);
        layout.addView(titulo);

        TextView contenido = new TextView(this);
        contenido.setText(construirResumen());
        contenido.setTextSize(16);
        contenido.setLineSpacing(0, 1.3f);
        layout.addView(contenido);

        Button btnCerrar = new Button(this);
        btnCerrar.setText("Cerrar");
        btnCerrar.setPadding(0, 20, 0, 0);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        layout.addView(btnCerrar);

        dialog.setContentView(layout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        dialog.show();
    }

    // 🔹 Construye el texto del resumen
    private String construirResumen() {
        String resumen = "✅ Datos capturados:\n\n";
        resumen += "• Nivel: " + sbNivel.getProgress() + "%\n";
        resumen += "• Aceptar: " + (cbAceptar.isChecked() ? "✔ Sí" : "✘ No") + "\n";

        int sexoId = rgSexo.getCheckedRadioButtonId();
        if (sexoId == R.id.rbM) resumen += "• Sexo: Masculino\n";
        else if (sexoId == R.id.rbF) resumen += "• Sexo: Femenino\n";
        else resumen += "• Sexo: (No seleccionado)\n";

        resumen += "• Estado: " + (tbInterruptor.isChecked() ? "🟢 ACTIVO" : "🔴 INACTIVO") + "\n";

        long fecha = cvFecha.getDate();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        resumen += "• Fecha: " + sdf.format(new java.util.Date(fecha)) + "\n";

        resumen += "• Calificación: " + rbCalificacion.getRating() + " ⭐\n";

        return resumen;
    }

    // 🔹 Método auxiliar para mostrar Toasts
    private void imprimirMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}