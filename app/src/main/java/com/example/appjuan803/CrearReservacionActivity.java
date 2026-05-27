package com.example.appjuan803;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appjuan803.models.Reservacion;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity para crear una nueva reservación
 */
public class CrearReservacionActivity extends AppCompatActivity {

    private static final String TAG = "CrearReservacionActivity";

    private EditText etEvento;
    private EditText etFecha;
    private EditText etLugar;
    private EditText etDescripcion;
    private Spinner spTipoEvento;
    private TextView tvTituloFormulario;
    private Button btnSeleccionarFecha;
    private Button btnGuardar;
    private Button btnCancelar;
    private ProgressBar progressBar;

    private String token;
    private String fechaSeleccionada;
    private boolean editMode;
    private int reservacionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reservacion);

        Log.d(TAG, "onCreate: Inicializando Activity de Crear Reservación");

        // Obtener token y modo del intent
        token = getIntent().getStringExtra("token");
        editMode = getIntent().getBooleanExtra("edit_mode", false);
        reservacionId = getIntent().getIntExtra("reservacion_id", -1);

        // Inicializar vistas
        initializeView();

        // Configurar listeners
        setupListeners();

        loadEditDataIfNeeded();
    }

    /**
     * Inicializar las vistas del layout
     */
    private void initializeView() {
        etEvento = findViewById(R.id.etEvento);
        etFecha = findViewById(R.id.etFecha);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        spTipoEvento = findViewById(R.id.spTipoEvento);
        tvTituloFormulario = findViewById(R.id.tvTituloFormulario);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        progressBar = findViewById(R.id.progressBar);

        // EditText de fecha es read-only
        etFecha.setFocusable(false);
        etFecha.setClickable(true);
    }

    private void loadEditDataIfNeeded() {
        if (!editMode) {
            return;
        }

        tvTituloFormulario.setText("EDITAR EVENTO");
        btnGuardar.setText("ACTUALIZAR");

        etEvento.setText(getIntent().getStringExtra("evento"));
        seleccionarTipoEvento(getIntent().getStringExtra("tipo_evento"));
        fechaSeleccionada = getIntent().getStringExtra("fecha");
        etFecha.setText(fechaSeleccionada);
        etLugar.setText(getIntent().getStringExtra("lugar"));
        etDescripcion.setText(getIntent().getStringExtra("descripcion"));
    }

    /**
     * Configurar listeners de botones
     */
    private void setupListeners() {
        // Botón de calendario
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarReservacion());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    /**
     * Mostrar DatePicker para seleccionar fecha
     */
    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaSeleccionada = sdf.format(calendar.getTime());

            etFecha.setText(fechaSeleccionada);
            Log.d(TAG, "Fecha seleccionada: " + fechaSeleccionada);
        };

        new DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /**
     * Guardar la nueva reservación
     */
    private void guardarReservacion() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Mostrar progreso
        showProgress(true);

        // Crear objeto de reservación
        Reservacion reservacion = new Reservacion();
        reservacion.setTipoEvento(spTipoEvento.getSelectedItem().toString());
        reservacion.setEvento(etEvento.getText().toString().trim());
        reservacion.setFecha(fechaSeleccionada);
        reservacion.setLugar(etLugar.getText().toString().trim());
        reservacion.setDescripcion(etDescripcion.getText().toString().trim());

        // Llamar API
        ApiService apiService = RetrofitClient.getApiService();
        String authorization = "Bearer " + token;

        Call<Reservacion> call = editMode
                ? apiService.actualizarReservacion(authorization, reservacionId, reservacion)
                : apiService.crearReservacion(authorization, reservacion);

        call.enqueue(new Callback<Reservacion>() {
            @Override
            public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    Reservacion nueva = response.body();

                    Toast.makeText(
                        CrearReservacionActivity.this,
                        editMode ? "Evento actualizado exitosamente" : "Reservación creada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show();

                    Log.d(TAG, "Reservación guardada con ID: " + nueva.getId());

                    // Retornar con resultado OK
                    setResult(RESULT_OK);
                    finish();

                } else {
                    String errorMessage = editMode ? "Error al actualizar el evento" : "Error al crear la reservación";
                    if (response.code() == 400) {
                        errorMessage = "Datos inválidos";
                    } else if (response.code() == 401) {
                        errorMessage = "No autorizado";
                    }

                    Toast.makeText(
                        CrearReservacionActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show();

                    Log.e(TAG, "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Reservacion> call, Throwable t) {
                showProgress(false);

                String errorMessage = "Error de conexión: " + t.getMessage();
                Toast.makeText(
                    CrearReservacionActivity.this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show();

                Log.e(TAG, "Error en llamada API", t);
            }
        });
    }

    /**
     * Validar campos del formulario
     */
    private boolean validarCampos() {
        boolean valido = true;

        String evento = etEvento.getText().toString().trim();
        if (evento.isEmpty()) {
            etEvento.setError("El nombre del evento es requerido");
            valido = false;
        }

        if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
            etFecha.setError("La fecha es requerida");
            valido = false;
        }

        String lugar = etLugar.getText().toString().trim();
        if (lugar.isEmpty()) {
            etLugar.setError("El lugar es requerido");
            valido = false;
        }

        return valido;
    }

    /**
     * Mostrar u ocultar barra de progreso
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        btnGuardar.setEnabled(!show);
        btnCancelar.setEnabled(!show);
        etEvento.setEnabled(!show);
        etFecha.setEnabled(!show);
        etLugar.setEnabled(!show);
        etDescripcion.setEnabled(!show);
        spTipoEvento.setEnabled(!show);
    }

    private void seleccionarTipoEvento(String tipoEvento) {
        if (tipoEvento == null || tipoEvento.trim().isEmpty()) {
            return;
        }

        for (int i = 0; i < spTipoEvento.getCount(); i++) {
            if (tipoEvento.equalsIgnoreCase(String.valueOf(spTipoEvento.getItemAtPosition(i)))) {
                spTipoEvento.setSelection(i);
                return;
            }
        }
    }
}

