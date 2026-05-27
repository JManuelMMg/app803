package com.example.appjuan803.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo para la respuesta de lista de reservaciones
 */
public class ReservacionListResponse {

    @SerializedName("total")
    private int total;

    @SerializedName("reservaciones")
    private List<Reservacion> reservaciones;

    public ReservacionListResponse() {
    }

    public ReservacionListResponse(int total, List<Reservacion> reservaciones) {
        this.total = total;
        this.reservaciones = reservaciones;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Reservacion> getReservaciones() {
        return reservaciones;
    }

    public void setReservaciones(List<Reservacion> reservaciones) {
        this.reservaciones = reservaciones;
    }
}

