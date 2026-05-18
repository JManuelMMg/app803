package com.example.appjuan803;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Application global para capturar excepciones no controladas y evitar que la app muera sin aviso.
 * Reinicia la pantalla principal en vez de cerrar bruscamente.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // Mostrar mensaje al usuario en hilo UI
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Error inesperado: se reiniciará la app", Toast.LENGTH_LONG).show());

            // Intentar reiniciar actividad principal después de pequeño retardo
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Intent i = new Intent(getApplicationContext(), menu_inicio.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } catch (Exception e) {
                    // Si no se puede reiniciar, no hacer nada y dejar que el proceso termine
                }
            }, 800);

            // Dormir un instante para permitir mostrar Toast y luego terminar proceso
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            // Evitar relanzamiento en bucle: terminar proceso
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);
        });
    }
}

