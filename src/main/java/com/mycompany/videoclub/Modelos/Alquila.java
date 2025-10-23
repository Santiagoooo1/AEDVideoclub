package com.mycompany.videoclub.Modelos;

import java.sql.Date;

public class Alquila {

    private int idAlquilan;
    private int idCliente;
    private int idSku; 
    private Date fechaAlquiler;
    private Date fechaLimiteDevolucion;
    private Date fechaDevolucionReal;

    public Alquila() {
    }

    public Alquila(int idCliente, int idSku, Date fechaAlquiler, Date fechaLimiteDevolucion, Date fechaDevolucionReal) {
        this.idCliente = idCliente;
        this.idSku = idSku;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaLimiteDevolucion = fechaLimiteDevolucion;
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public Alquila(int idAlquilan, int idCliente, int idSku, Date fechaAlquiler, Date fechaLimiteDevolucion, Date fechaDevolucionReal) {
        this.idAlquilan = idAlquilan;
        this.idCliente = idCliente;
        this.idSku = idSku;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaLimiteDevolucion = fechaLimiteDevolucion;
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public int getIdAlquilan() {
        return idAlquilan;
    }

    public void setIdAlquilan(int idAlquilan) {
        this.idAlquilan = idAlquilan;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdSku() {
        return idSku;
    }

    public void setIdSku(int idSku) {
        this.idSku = idSku;
    }

    public Date getFechaAlquiler() {
        return fechaAlquiler;
    }

    public void setFechaAlquiler(Date fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
    }

    public Date getFechaLimiteDevolucion() {
        return fechaLimiteDevolucion;
    }

    public void setFechaLimiteDevolucion(Date fechaLimiteDevolucion) {
        this.fechaLimiteDevolucion = fechaLimiteDevolucion;
    }

    public Date getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Date fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    @Override
    public String toString() {
        return "Alquila{" +
                "idAlquilan=" + idAlquilan +
                ", idCliente=" + idCliente +
                ", idSku=" + idSku +
                ", fechaAlquiler=" + fechaAlquiler +
                ", fechaLimiteDevolucion=" + fechaLimiteDevolucion +
                ", fechaDevolucionReal=" + fechaDevolucionReal +
                '}';
    }
}


