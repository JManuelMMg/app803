package com.example.appjuan803;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appjuan803.models.LoginRequest;
import com.example.appjuan803.models.LoginResponse;
import com.example.appjuan803.models.RegisterRequest;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity de autenticación (Login)
 * Permite que el usuario inicie sesión con correo y contraseña
 * Almacena el token JWT de forma segura
 */
public class Eventos extends AppCompatActivity {

    private static final String TAG = "Eventos";
    private static final String PREFS_NAME = "AppReservacionesPrefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROL = "user_rol";

    private EditText etCorreo;
    private EditText etPassword;
    private EditText etNombre;
    private TextView tvTituloAuth;
    private TextView tvSubtituloAuth;
    private LinearLayout containerNombre;
    private Button btnLogin;
    private Button btnToggleRegister;
    private ProgressBar progressBar;
    private boolean registerMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        Log.d(TAG, "onCreate: Inicializando Activity de Login");

        // Inicializar vistas
        initializeView();

        // Configurar listeners
        setupListeners();

        // Verificar si ya existe sesión activa
        checkExistingSession();
    }

    /**
     * Inicializar las vistas del layout
     */
    private void initializeView() {
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        tvTituloAuth = findViewById(R.id.tvTituloAuth);
        tvSubtituloAuth = findViewById(R.id.tvSubtituloAuth);
        containerNombre = findViewById(R.id.containerNombre);
        btnLogin = findViewById(R.id.btnLogin);
        btnToggleRegister = findViewById(R.id.btnToggleRegister);
        progressBar = findViewById(R.id.progressBar);

        // Configurar valores por defecto para pruebas
        etCorreo.setText("admin@gmail.com");
        etPassword.setText("123456");
    }

    /**
     * Configurar listeners de botones
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (registerMode) {
                performRegister();
            } else {
                performLogin();
            }
        });
        btnToggleRegister.setOnClickListener(v -> toggleRegisterMode());
    }

    /**
     * Realizar el login
     */
    private void performLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos
        if (!validateInputs(correo, password)) {
            return;
        }

        // Mostrar progreso
        showProgress(true);

        // Crear request
        LoginRequest loginRequest = new LoginRequest(correo, password);

        // Llamar API
        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Guardar token y datos del usuario
                    saveSessionData(loginResponse);

                    Toast.makeText(
                        Eventos.this,
                        "Login exitoso",
                        Toast.LENGTH_SHORT
                    ).show();

                    Log.d(TAG, "Login exitoso para: " + correo);

                    // Ir a la activity de reservaciones
                    navigateToReservaciones();
                } else {
                    String errorMessage = "Error en la autenticación";
                    if (response.code() == 401) {
                        errorMessage = "Credenciales inválidas";
                    }

                    Toast.makeText(
                        Eventos.this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show();

                    Log.e(TAG, "Error en login: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgress(false);

                String errorMessage = "Error de conexión: " + t.getMessage();
                Toast.makeText(
                    Eventos.this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show();

                Log.e(TAG, "Error en llamada API", t);
            }
        });
    }

    private void performRegister() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateRegisterInputs(nombre, correo, password)) {
            return;
        }

        showProgress(true);

        RegisterRequest registerRequest = new RegisterRequest(nombre, correo, password);
        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveSessionData(loginResponse);

                    Toast.makeText(
                        Eventos.this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_SHORT
                    ).show();

                    Log.d(TAG, "Registro exitoso para: " + correo);
                    navigateToReservaciones();
                } else {
                    String errorMessage = "No se pudo crear la cuenta";
                    if (response.code() == 409) {
                        errorMessage = "Ese correo ya está registrado";
                    } else if (response.code() == 400) {
                        errorMessage = "Revisa los datos ingresados";
                    }

                    Toast.makeText(Eventos.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error en registro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgress(false);

                Toast.makeText(
                    Eventos.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_LONG
                ).show();

                Log.e(TAG, "Error en llamada API de registro", t);
            }
        });
    }

    /**
     * Validar que los campos de entrada sean válidos
     */
    private boolean validateInputs(String correo, String password) {
        if (correo.isEmpty()) {
            etCorreo.setError("El correo es requerido");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Ingresa un correo válido");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("La contraseña es requerida");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    private boolean validateRegisterInputs(String nombre, String correo, String password) {
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es requerido");
            return false;
        }

        if (nombre.length() < 2) {
            etNombre.setError("Ingresa un nombre válido");
            return false;
        }

        return validateInputs(correo, password);
    }

    /**
     * Guardar datos de sesión en SharedPreferences
     */
    private void saveSessionData(LoginResponse loginResponse) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (loginResponse.getToken() != null) {
            editor.putString(KEY_TOKEN, loginResponse.getToken().getAccessToken());
        }

        if (loginResponse.getUsuario() != null) {
            LoginResponse.UsuarioData usuario = loginResponse.getUsuario();
            editor.putInt(KEY_USER_ID, usuario.getId());
            editor.putString(KEY_USER_NAME, usuario.getNombre());
            editor.putString(KEY_USER_ROL, usuario.getRol());
        }

        editor.apply();

        Log.d(TAG, "Datos de sesión guardados");
    }

    /**
     * Verificar si ya existe una sesión activa
     */
    private void checkExistingSession() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        if (token != null && !token.isEmpty()) {
            Log.d(TAG, "Sesión activa encontrada, navigando a Reservaciones");
            navigateToReservaciones();
        }
    }

    /**
     * Navegar a la activity de reservaciones
     */
    private void navigateToReservaciones() {
        Intent intent = new Intent(Eventos.this, ReservacionesActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Mostrar u ocultar barra de progreso
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        btnLogin.setEnabled(!show);
        btnToggleRegister.setEnabled(!show);
        etNombre.setEnabled(!show);
        etCorreo.setEnabled(!show);
        etPassword.setEnabled(!show);
    }

    private void toggleRegisterMode() {
        registerMode = !registerMode;

        containerNombre.setVisibility(registerMode ? android.view.View.VISIBLE : android.view.View.GONE);
        tvTituloAuth.setText(registerMode ? "CREAR CUENTA" : "INICIAR SESIÓN");
        tvSubtituloAuth.setText(registerMode ? "Regístrate como cliente para reservar eventos" : "Ingresa tus credenciales");
        btnLogin.setText(registerMode ? "REGISTRARME" : "INICIAR SESIÓN");
        btnToggleRegister.setText(registerMode ? "YA TENGO CUENTA" : "CREAR CUENTA NUEVA");

        etNombre.setError(null);
        etCorreo.setError(null);
        etPassword.setError(null);
    }
}

