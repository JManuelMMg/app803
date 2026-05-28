package com.example.appjuan803;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.appjuan803.models.Evento;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEventoActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etTipo;
    private EditText etFecha;
    private EditText etLugar;
    private EditText etCapacidad;
    private EditText etDescripcion;
    private TextView tvTitulo;
    private ProgressBar progressBar;
    private AppCompatButton btnGuardar;
    private AppCompatButton btnCancelar;

    private String token;
    private boolean editMode;
    private int eventId;
    private String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        token = getIntent().getStringExtra("token");
        editMode = getIntent().getBooleanExtra("edit_mode", false);
        eventId = getIntent().getIntExtra("event_id", -1);

        initViews();
        setupListeners();
        loadEditData();
    }

    private void initViews() {
        tvTitulo = findViewById(R.id.tvTituloEventoForm);
        etTitulo = findViewById(R.id.etTituloEvento);
        etTipo = findViewById(R.id.etTipoEvento);
        etFecha = findViewById(R.id.etFechaEvento);
        etLugar = findViewById(R.id.etLugarEvento);
        etCapacidad = findViewById(R.id.etCapacidadEvento);
        etDescripcion = findViewById(R.id.etDescripcionEvento);
        progressBar = findViewById(R.id.progressEvento);
        btnGuardar = findViewById(R.id.btnGuardarEvento);
        btnCancelar = findViewById(R.id.btnCancelarEvento);
    }

    private void setupListeners() {
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarEvento());
    }

    private void loadEditData() {
        if (!editMode) {
            return;
        }

        tvTitulo.setText("EDITAR EVENTO");
        btnGuardar.setText("ACTUALIZAR");
        etTitulo.setText(getIntent().getStringExtra("titulo"));
        etTipo.setText(getIntent().getStringExtra("tipo_evento"));
        fechaSeleccionada = getIntent().getStringExtra("fecha");
        etFecha.setText(fechaSeleccionada);
        etLugar.setText(getIntent().getStringExtra("lugar"));
        etDescripcion.setText(getIntent().getStringExtra("descripcion"));
        int capacidad = getIntent().getIntExtra("capacidad", -1);
        if (capacidad >= 0) {
            etCapacidad.setText(String.valueOf(capacidad));
        }
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            fechaSeleccionada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            etFecha.setText(fechaSeleccionada);
        };
        new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void guardarEvento() {
        if (!validar()) {
            return;
        }

        Evento evento = new Evento();
        evento.setTitulo(etTitulo.getText().toString().trim());
        evento.setTipoEvento(etTipo.getText().toString().trim().isEmpty() ? "Evento general" : etTipo.getText().toString().trim());
        evento.setFecha(fechaSeleccionada);
        evento.setLugar(etLugar.getText().toString().trim());
        evento.setDescripcion(etDescripcion.getText().toString().trim());

        String capacidadText = etCapacidad.getText().toString().trim();
        if (!capacidadText.isEmpty()) {
            evento.setCapacidad(Integer.parseInt(capacidadText));
        }

        progressBar.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        ApiService apiService = RetrofitClient.getApiService();
        String authorization = "Bearer " + token;
        Call<Evento> call = editMode
                ? apiService.actualizarEvento(authorization, eventId, evento)
                : apiService.crearEvento(authorization, evento);

        call.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                progressBar.setVisibility(View.GONE);
                btnGuardar.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CrearEventoActivity.this, editMode ? "Evento actualizado" : "Evento publicado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CrearEventoActivity.this, "No se pudo guardar el evento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGuardar.setEnabled(true);
                Toast.makeText(CrearEventoActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validar() {
        if (etTitulo.getText().toString().trim().isEmpty()) {
            etTitulo.setError("Título requerido");
            return false;
        }
        if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
            etFecha.setError("Fecha requerida");
            return false;
        }
        if (etLugar.getText().toString().trim().isEmpty()) {
            etLugar.setError("Lugar requerido");
            return false;
        }
        return true;
    }
}
