package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para la respuesta de login
 * Contiene el token JWT y los datos del usuario
 */
public class LoginResponse {

    @SerializedName("token")
    private TokenData token;

    @SerializedName("usuario")
    private UsuarioData usuario;

    @SerializedName("message")
    private String message;

    // Getters y Setters
    public TokenData getToken() {
        return token;
    }

    public void setToken(TokenData token) {
        this.token = token;
    }

    public UsuarioData getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioData usuario) {
        this.usuario = usuario;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ========== CLASE INTERNA: TokenData ==========

    public static class TokenData {

        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("expires_in")
        private int expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }
    }

    // ========== CLASE INTERNA: UsuarioData ==========

    public static class UsuarioData {

        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("correo")
        private String correo;

        @SerializedName("rol")
        private String rol;

        @SerializedName("created_at")
        private String createdAt;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}

