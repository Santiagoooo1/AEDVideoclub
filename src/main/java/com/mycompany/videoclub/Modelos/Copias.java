package com.mycompany.videoclub.Modelos;

public class Copias {

    private int idSku;              
    private boolean disponibilidad; 
    private String estado;          
    private int idPelicula;         

    public Copias() {
    }

    public Copias(boolean disponibilidad, String estado, int idPelicula) {
        this.disponibilidad = disponibilidad;
        this.estado = estado;
        this.idPelicula = idPelicula;
    }

    public Copias(int idSku, boolean disponibilidad, String estado, int idPelicula) {
        this.idSku = idSku;
        this.disponibilidad = disponibilidad;
        this.estado = estado;
        this.idPelicula = idPelicula;
    }

    public int getIdSku() {
        return idSku;
    }

    public void setIdSku(int idSku) {
        this.idSku = idSku;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    @Override
    public String toString() {
        return "Copias{" +
                "idSku=" + idSku +
                ", disponibilidad=" + disponibilidad +
                ", estado='" + estado + '\'' +
                ", idPelicula=" + idPelicula +
                '}';
    }
}

