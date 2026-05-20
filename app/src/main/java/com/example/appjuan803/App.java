package com.example.appjuan803;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Application global para capturar excepciones no controladas y evitar que la app muera sin aviso.
 * Reinicia la pantalla principal en vez de cerrar bruscamente.
 */
public class App extends Application {

        private static final String TAG = "AppJuan803";
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // Loggear el error para debugging
            Log.e(TAG, "Excepción no controlada en hilo: " + thread.getName(), throwable);

            // Guardar stacktrace en archivo para que el desarrollador lo revise
            try {
                java.io.File f = new java.io.File(getFilesDir(), "last_crash.txt");
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(f, false))) {
                    throwable.printStackTrace(pw);
                }
                Log.i(TAG, "Stacktrace escrito en " + f.getAbsolutePath());
            } catch (Exception io) {
                Log.w(TAG, "No se pudo escribir el stacktrace en archivo", io);
            }

            // Mostrar mensaje al usuario en hilo UI (intento de ser amable)
            new Handler(Looper.getMainLooper()).post(() -> {
                String errorMsg = "Error: " + throwable.getClass().getSimpleName();
                if (throwable.getMessage() != null) {
                    errorMsg += " - " + throwable.getMessage();
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            });

            // Intentar reiniciar a login después de pequeño retardo para evitar loops rápidos
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Intent i = new Intent(getApplicationContext(), login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } catch (Exception e) {
                    Log.e(TAG, "No se pudo reiniciar a login", e);
                }
            }, 1200);

            // Dar tiempo para que el Toast se muestre y luego delegar al handler por defecto
            try {
                Thread.sleep(1800);
            } catch (InterruptedException ignored) {}

            if (defaultExceptionHandler != null) {
                defaultExceptionHandler.uncaughtException(thread, throwable);
            }
        });
    }
}

