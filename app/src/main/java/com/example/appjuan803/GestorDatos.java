package com.example.appjuan803;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorDatos {
    private static final String PREFS_NAME = "AppJuan803Prefs";
    private static final String KEY_SALDO = "saldo_actual";
    private static final String KEY_NOMBRE_USUARIO = "nombre_usuario";
    private static final String KEY_EMAIL_USUARIO = "email_usuario";

    private SharedPreferences prefs;

    public GestorDatos(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Gestión de Saldo
    public double obtenerSaldo() {
        return prefs.getFloat(KEY_SALDO, 1000.0f); // Saldo inicial por defecto
    }

    public void guardarSaldo(double saldo) {
        prefs.edit().putFloat(KEY_SALDO, (float) saldo).apply();
    }

    public void agregarSaldo(double cantidad) {
        double saldoActual = obtenerSaldo();
        guardarSaldo(saldoActual + cantidad);
    }

    public boolean restarSaldo(double cantidad) {
        double saldoActual = obtenerSaldo();
        if (saldoActual >= cantidad) {
            guardarSaldo(saldoActual - cantidad);
            return true;
        }
        return false;
    }

    // Gestión de Perfil
    public String obtenerNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, "Juan Pérez");
    }

    public void guardarNombreUsuario(String nombre) {
        prefs.edit().putString(KEY_NOMBRE_USUARIO, nombre).apply();
    }

    public String obtenerEmailUsuario() {
        return prefs.getString(KEY_EMAIL_USUARIO, "juan@example.com");
    }

    public void guardarEmailUsuario(String email) {
        prefs.edit().putString(KEY_EMAIL_USUARIO, email).apply();
    }
}
