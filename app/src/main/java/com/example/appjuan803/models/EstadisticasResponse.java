package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Modelo para las estadísticas del dashboard
 */
public class EstadisticasResponse {

    @SerializedName("total_reservaciones")
    private int totalReservaciones;

    @SerializedName("total_usuarios")
    private int totalUsuarios;

    @SerializedName("reservaciones_por_mes")
    private List<Map<String, Object>> reservacionesPorMes;

    @SerializedName("eventos_mas_populares")
    private List<Map<String, Object>> eventosMasPopulares;

    @SerializedName("lugares_mas_reservados")
    private List<Map<String, Object>> lugaresMasReservados;

    @SerializedName("ocupacion_eventos")
    private List<Map<String, Object>> ocupacionEventos;

    @SerializedName("reservas_por_evento_usuario")
    private List<Map<String, Object>> reservasPorEventoUsuario;

    public EstadisticasResponse() {
    }

    public int getTotalReservaciones() {
        return totalReservaciones;
    }

    public void setTotalReservaciones(int totalReservaciones) {
        this.totalReservaciones = totalReservaciones;
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public List<Map<String, Object>> getReservacionesPorMes() {
        return reservacionesPorMes;
    }

    public void setReservacionesPorMes(List<Map<String, Object>> reservacionesPorMes) {
        this.reservacionesPorMes = reservacionesPorMes;
    }

    public List<Map<String, Object>> getEventosMasPopulares() {
        return eventosMasPopulares;
    }

    public void setEventosMasPopulares(List<Map<String, Object>> eventosMasPopulares) {
        this.eventosMasPopulares = eventosMasPopulares;
    }

    public List<Map<String, Object>> getLugaresMasReservados() {
        return lugaresMasReservados;
    }

    public void setLugaresMasReservados(List<Map<String, Object>> lugaresMasReservados) {
        this.lugaresMasReservados = lugaresMasReservados;
    }

    public List<Map<String, Object>> getOcupacionEventos() {
        return ocupacionEventos;
    }

    public void setOcupacionEventos(List<Map<String, Object>> ocupacionEventos) {
        this.ocupacionEventos = ocupacionEventos;
    }

    public List<Map<String, Object>> getReservasPorEventoUsuario() {
        return reservasPorEventoUsuario;
    }

    public void setReservasPorEventoUsuario(List<Map<String, Object>> reservasPorEventoUsuario) {
        this.reservasPorEventoUsuario = reservasPorEventoUsuario;
    }
}

