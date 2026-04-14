package com.example.appjuan803;

import java.io.Serializable;

public class Gasto implements Serializable {
    private String descripcion;
    private double monto;
    private String categoria;
    private String fecha;
    private long id;

    public Gasto() {}

    public Gasto(String descripcion, double monto, String categoria, String fecha) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
        this.id = System.currentTimeMillis(); // ID único basado en timestamp
    }

    // Getters y Setters
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Override
    public String toString() {
        return descripcion + " - $" + String.format("%.2f", monto) + " (" + categoria + ")";
    }
}
