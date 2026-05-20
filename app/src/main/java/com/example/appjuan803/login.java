package com.example.appjuan803;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;

public class login extends AppCompatActivity {

    // Declarar variables
    Button btnIngresar, btnCancelar, btnBiometricLogin;
    EditText txtUsuario, txtPassword;
    String user, password;

    // Variables para autenticación biométrica
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Animación de entrada elegante
        overridePendingTransition(R.anim.login_elegant_enter, 0);

        // Vincular variables con layout
        btnIngresar = findViewById(R.id.Ingresar);
        btnCancelar = findViewById(R.id.Cancelar);
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);

        // Obtener vibrador
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Aplicar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat.Builder builder = new WindowInsetsCompat.Builder(insets);
            return builder.build();
        });

        // Configurar autenticación biométrica
        setupBiometricAuth();

        // Evento Cancelar con animación
        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> {
                try {
                    animarBoton(btnCancelar);
                } catch (Exception ignored) {}
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
            });
        } else {
            Log.w("LoginInit", "btnCancelar es null en layout");
        }

        // Evento Ingresar (tradicional)
        if (btnIngresar != null) {
            btnIngresar.setOnClickListener(v -> {
                try {
                    animarBoton(btnIngresar);
                } catch (Exception ignored) {}
                loginWithCredentials();
            });
        } else {
            Log.w("LoginInit", "btnIngresar es null en layout");
        }

        // Evento Ingresar con Huella
        if (btnBiometricLogin != null) {
            btnBiometricLogin.setOnClickListener(v -> {
                try {
                    animarBoton(btnBiometricLogin);
                } catch (Exception ignored) {}
                checkBiometricAvailabilityAndAuthenticate();
            });
        } else {
            Log.w("LoginInit", "btnBiometricLogin es null en layout");
        }
    }

    // 🔹 Animación de botón
    private void animarBoton(Button button) {
        button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_press));
        vibracion(30); // Vibración corta
    }

    // 🔹 Login con usuario y contraseña
    private void loginWithCredentials() {
        try {
            user = txtUsuario.getText().toString().trim();
            password = txtPassword.getText().toString().trim();

            if (user.isEmpty() || password.isEmpty()) {
                imprimirmensaje("⚠️  Por favor ingresa usuario y contraseña");
                vibracion(50);
                return;
            }

            if (user.equals("Juan") && password.equals("12345")) {
                vibracion(new long[]{0, 50, 100, 50}); // Patrón de vibración exitoso
                imprimirmensaje("✅ ¡Bienvenido " + user + "!!");

                // Guardar sesión con validación
                try {
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("logged_in", true);
                    editor.apply();
                    Log.i("LoginSuccess", "Sesión guardada correctamente");
                } catch (Exception e) {
                    Log.e("LoginError", "Error al guardar sesión", e);
                }

                // Transición suave
                proceedToMainActivity();
            } else {
                vibracion(new long[]{0, 100, 100, 100}); // Patrón de vibración error
                imprimirmensaje("❌ Datos Incorrectos");
                limpiarCajas();
                agitarCampos();
            }
        } catch (Exception e) {
            Log.e("LoginError", "Error durante el login", e);
            imprimirmensaje("⚠️  Ocurrió un error: " + e.getMessage());
            vibracion(100);
        }
    }

    // 🔹 Vibración del dispositivo
    private void vibracion(long milliseconds) {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(milliseconds,
                        android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        }
    }

    // 🔹 Patrón de vibración
    private void vibracion(long[] pattern) {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    // 🔹 Agitar campos en error
    private void agitarCampos() {
        txtUsuario.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        txtPassword.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
    }

    // 🔹 Configurar autenticación biométrica
    private void setupBiometricAuth() {
        try {
            executor = ContextCompat.getMainExecutor(this);

            biometricPrompt = new BiometricPrompt(this, executor,
                    new BiometricPrompt.AuthenticationCallback() {

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            runOnUiThread(() -> {
                                Log.w("BiometricAuth", "Error biométrico: " + errString);
                                imprimirmensaje("❌ " + errString);
                                vibracion(new long[]{0, 100, 100, 100});
                            });
                        }

                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            runOnUiThread(() -> {
                                Log.i("BiometricAuth", "Autenticación biométrica exitosa");
                                vibracion(new long[]{0, 50, 100, 50}); // Patrón de éxito
                                imprimirmensaje("✅ ¡Autenticación exitosa!");
                                proceedToMainActivity();
                            });
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            runOnUiThread(() -> {
                                Log.w("BiometricAuth", "Huella no reconocida");
                                imprimirmensaje("⚠️  Huella no reconocida");
                                vibracion(150);
                            });
                        }
                    });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("🔐 Autenticación Biométrica")
                    .setSubtitle("Verifica tu identidad")
                    .setDescription("Coloca tu dedo en el sensor biométrico")
                    .setNegativeButtonText("Cancelar")
                    .setConfirmationRequired(false)
                    .build();

            Log.i("BiometricAuth", "Autenticación biométrica configurada correctamente");
        } catch (Exception e) {
            Log.e("BiometricAuth", "Error al configurar autenticación biométrica", e);
            imprimirmensaje("⚠️  Error al configurar biometría");
        }
    }

    // 🔹 Verificar disponibilidad y autenticar
    private void checkBiometricAvailabilityAndAuthenticate() {
        try {
            BiometricManager biometricManager = BiometricManager.from(this);

            int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
                    | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

            switch (biometricManager.canAuthenticate(authenticators)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.i("BiometricAuth", "Iniciando autenticación biométrica");
                    try {
                        if (biometricPrompt != null && promptInfo != null) {
                            biometricPrompt.authenticate(promptInfo);
                        } else {
                            Log.w("BiometricAuth", "biometricPrompt o promptInfo es null");
                            imprimirmensaje("⚠️  Biometría no disponible");
                        }
                    } catch (Exception e) {
                        Log.e("BiometricAuth", "Error al invocar biometricPrompt", e);
                        imprimirmensaje("⚠️  Error biométrico: " + e.getMessage());
                    }
                    break;

                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.w("BiometricAuth", "Sin sensor biométrico");
                    imprimirmensaje("⚠️  Dispositivo sin sensor biométrico");
                    vibracion(100);
                    break;

                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.w("BiometricAuth", "Sensor no disponible");
                    imprimirmensaje("⚠️  Sensor no disponible");
                    vibracion(100);
                    break;

                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Log.w("BiometricAuth", "Sin huellas registradas");
                    imprimirmensaje("⚠️  Registra tu huella en Ajustes");
                    vibracion(100);
                    break;

                default:
                    Log.w("BiometricAuth", "Error desconocido");
                    imprimirmensaje("⚠️  Error al acceder a la biometría");
                    vibracion(100);
                    break;
            }
        } catch (Exception e) {
            Log.e("BiometricAuth", "Error al verificar disponibilidad biométrica", e);
            imprimirmensaje("⚠️  Error: " + e.getMessage());
            vibracion(100);
        }
    }

    // 🔹 Proceder a la actividad principal después de autenticar
    private void proceedToMainActivity() {
        try {
            Log.i("LoginSuccess", "Navegando a menu_inicio");

            // Guardar sesión
            try {
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("logged_in", true);
                editor.apply();
                Log.i("LoginSuccess", "Sesión guardada correctamente");
            } catch (Exception e) {
                Log.e("LoginError", "Error al guardar sesión", e);
            }

            Intent intent = new Intent(login.this, menu_inicio.class);
            // Limpiar stack para evitar actividades residuales que puedan causar loops
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

            Log.i("LoginSuccess", "Navegación a menu_inicio completada");
        } catch (Exception e) {
            Log.e("LoginError", "Error al navegar a menu_inicio", e);
            imprimirmensaje("⚠️  Error al acceder al menú: " + e.getMessage());
            vibracion(100);
        }
    }

    // 🔹 Mostrar mensaje con Toast
    private void imprimirmensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    // 🔹 Limpiar campos de texto
    private void limpiarCajas() {
        txtUsuario.setText("");
        txtPassword.setText("");
        txtUsuario.requestFocus();
    }
}