package com.mycompany.videoclub.Modelos;

public class Peliculas {
    private int idPelicula;
    private String titulo;
    private String director;
    private int anioLanzamiento;
    private String genero;
    private int cantidad;

    public Peliculas() {}

    public Peliculas(String titulo, String director, int anioLanzamiento, String genero, int cantidad) {
        this.titulo = titulo;
        this.director = director;
        this.anioLanzamiento = anioLanzamiento;
        this.genero = genero;
        this.cantidad = cantidad;
    }

    public Peliculas(int idPelicula, String titulo, String director, int anioLanzamiento, String genero, int cantidad) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.director = director;
        this.anioLanzamiento = anioLanzamiento;
        this.genero = genero;
        this.cantidad = cantidad;
    }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public int getAnioLanzamiento() { return anioLanzamiento; }
    public void setAnioLanzamiento(int anioLanzamiento) { this.anioLanzamiento = anioLanzamiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "Peliculas{" +
                "idPelicula=" + idPelicula +
                ", titulo='" + titulo + '\'' +
                ", director='" + director + '\'' +
                ", anioLanzamiento=" + anioLanzamiento +
                ", genero='" + genero + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}


