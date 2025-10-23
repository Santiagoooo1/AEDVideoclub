package com.mycompany.videoclub.DAO.Interfaces;

import com.mycompany.videoclub.Modelos.Peliculas;
import java.util.List;

public interface IPelicula {
    public boolean agregarPelicula(Peliculas p);
    public boolean eliminarPelicula(int idPelicula);
    public boolean actualizarPelicula(Peliculas p);
    Peliculas obtenerPelicula(int idPelicula);
    List<Peliculas>listarPelicula();
}
