package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para la solicitud de login
 */
public class LoginRequest {

    @SerializedName("correo")
    private String correo;

    @SerializedName("password")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

