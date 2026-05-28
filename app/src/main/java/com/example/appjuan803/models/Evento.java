package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;

public class Evento {

    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("tipo_evento")
    private String tipoEvento;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("lugar")
    private String lugar;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("capacidad")
    private Integer capacidad;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
