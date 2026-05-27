package com.example.appjuan803;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjuan803.adapter.ReservacionAdapter;
import com.example.appjuan803.models.Reservacion;
import com.example.appjuan803.models.ReservacionListResponse;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity principal de Reservaciones
 * Muestra la lista de reservaciones usando RecyclerView
 * Permite crear nuevas reservaciones
 * Muestra opciones diferentes según el rol del usuario
 */
public class ReservacionesActivity extends AppCompatActivity {

    private static final String TAG = "ReservacionesActivity";
    private static final String PREFS_NAME = "AppReservacionesPrefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER_ROL = "user_rol";

    private RecyclerView recyclerView;
    private ReservacionAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Button btnLogout;
    private Button btnCrearReservacion;
    private Button btnDashboard;
    private Button btnReservaciones;

    private ApiService apiService;
    private String token;
    private String userRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);

        Log.d(TAG, "onCreate: Inicializando Activity de Reservaciones");

        // Verificar token
        if (!checkToken()) {
            return;
        }

        // Inicializar vistas
        initializeView();

        // Cargar reservaciones
        loadReservaciones();

        // Configurar listeners
        setupListeners();

        // Configurar opciones según rol
        setupRoleOptions();
    }

    /**
     * Verificar si el token existe y el usuario está autenticado
     */
    private boolean checkToken() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        token = prefs.getString(KEY_TOKEN, null);
        userRol = prefs.getString(KEY_USER_ROL, "normal");

        if (token == null || token.isEmpty()) {
            Log.w(TAG, "No hay token de autenticación");
            Toast.makeText(this, "Sesión expirada. Por favor inicia sesión", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return false;
        }

        Log.d(TAG, "Token verificado. Rol: " + userRol);
        return true;
    }

    /**
     * Inicializar las vistas del layout
     */
    private void initializeView() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnLogout = findViewById(R.id.btnLogout);
        btnCrearReservacion = findViewById(R.id.btnCrearReservacion);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnReservaciones = findViewById(R.id.reservaciones);

        // Configurar RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        );
        recyclerView.setLayoutManager(layoutManager);

        // Crear adaptador
        adapter = new ReservacionAdapter(new ArrayList<>(), this, userRol);
        adapter.setToken(token);
        recyclerView.setAdapter(adapter);

        // Inicializar servicio API
        apiService = RetrofitClient.getApiService();
    }

    /**
     * Configurar listeners de botones
     */
    private void setupListeners() {
        // Botón Logout
        btnLogout.setOnClickListener(v -> performLogout());

        // Botón requerido: detona GET /api/reservaciones con Bearer token e infla el RecyclerView
        btnReservaciones.setOnClickListener(v -> loadReservaciones());

        // Botón Crear Reservación
        btnCrearReservacion.setOnClickListener(v -> abrirCrearReservacion());

        // Botón Dashboard (admin)
        btnDashboard.setOnClickListener(v -> abrirDashboard());

    }

    /**
     * Cargar reservaciones desde la API
     */
    private void loadReservaciones() {
        Log.d(TAG, "Cargando reservaciones...");
        showProgress(true);

        String authorization = "Bearer " + token;
        Call<ReservacionListResponse> call = apiService.listarReservaciones(authorization, 0, 50);

        call.enqueue(new Callback<ReservacionListResponse>() {
            @Override
            public void onResponse(
                Call<ReservacionListResponse> call,
                Response<ReservacionListResponse> response
            ) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    ReservacionListResponse data = response.body();
                    List<Reservacion> reservaciones = data.getReservaciones();

                    Log.d(TAG, "Reservaciones cargadas: " + reservaciones.size());

                    if (reservaciones.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setReservaciones(reservaciones);
                    }

                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code());
                    Toast.makeText(
                        ReservacionesActivity.this,
                        "Error al cargar reservaciones",
                        Toast.LENGTH_SHORT
                    ).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReservacionListResponse> call, Throwable t) {
                showProgress(false);

                Log.e(TAG, "Error en llamada API", t);
                Toast.makeText(
                    ReservacionesActivity.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Configurar opciones según el rol del usuario
     */
    private void setupRoleOptions() {
        if ("admin".equals(userRol)) {
            Log.d(TAG, "Usuario es ADMIN - mostrando todas las opciones");
            btnDashboard.setVisibility(View.VISIBLE);
            btnCrearReservacion.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Usuario es NORMAL - limitando opciones");
            btnDashboard.setVisibility(View.GONE);
            btnCrearReservacion.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Abrir activity de crear reservación
     */
    private void abrirCrearReservacion() {
        Intent intent = new Intent(this, CrearReservacionActivity.class);
        intent.putExtra("token", token);
        startActivityForResult(intent, 1);
    }

    /**
     * Abrir dashboard de estadísticas
     */
    private void abrirDashboard() {
        if (!"admin".equals(userRol)) {
            Toast.makeText(this, "No tienes permisos de administrador", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }

    /**
     * Realizar logout
     */
    private void performLogout() {
        // Limpiar datos de sesión
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Log.d(TAG, "Logout realizado");

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    /**
     * Redirigir al login
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, Eventos.class);
        startActivity(intent);
        finish();
    }

    /**
     * Mostrar u ocultar barra de progreso
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Manejar resultado de activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Recargar reservaciones
            loadReservaciones();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar al volver a la activity
        loadReservaciones();
    }
}

