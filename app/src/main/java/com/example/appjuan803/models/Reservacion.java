package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para una reservación
 */
public class Reservacion {

    @SerializedName("id")
    private int id;

    @SerializedName("evento")
    private String evento;

    @SerializedName("tipo_evento")
    private String tipoEvento;

    @SerializedName("event_id")
    private Integer eventId;

    @SerializedName("evento_id")
    private Integer eventoId;

    @SerializedName("fecha")
    private String fecha;  // Formato: YYYY-MM-DD

    @SerializedName("lugar")
    private String lugar;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("cantidad")
    private int cantidad = 1;

    @SerializedName("usuario_id")
    private int usuarioId;

    @SerializedName("usuario_nombre")
    private String usuarioNombre;

    @SerializedName("usuario_correo")
    private String usuarioCorreo;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Reservacion() {
    }

    public Reservacion(String evento, String fecha, String lugar) {
        this.evento = evento;
        this.tipoEvento = "Evento general";
        this.fecha = fecha;
        this.lugar = lugar;
    }

    public Reservacion(String evento, String fecha, String lugar, String descripcion) {
        this.evento = evento;
        this.tipoEvento = "Evento general";
        this.fecha = fecha;
        this.lugar = lugar;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad <= 0 ? 1 : cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioCorreo() {
        return usuarioCorreo;
    }

    public void setUsuarioCorreo(String usuarioCorreo) {
        this.usuarioCorreo = usuarioCorreo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Reservacion{" +
                "id=" + id +
                ", evento='" + evento + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", fecha='" + fecha + '\'' +
                ", lugar='" + lugar + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", usuarioId=" + usuarioId +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                '}';
    }
}

