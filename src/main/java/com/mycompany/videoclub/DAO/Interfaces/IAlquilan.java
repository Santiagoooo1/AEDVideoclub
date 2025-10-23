package com.mycompany.videoclub.DAO.Interfaces;

import com.mycompany.videoclub.Modelos.Alquila;
import java.sql.Date;
import java.util.List;

public interface IAlquilan {

    boolean agregarAlquiler(Alquila a);

    Alquila obtenerAlquiler(int idAlquila);       
    List<Alquila> listarAlquileres();             
    List<Alquila> listarPorCliente(int idCliente); 
    List<Alquila> listarPorPelicula(int idPelicula); 

    boolean actualizarAlquiler(Alquila a);              
    boolean registrarDevolucion(int idAlquila, Date fechaDevolucionReal); 

    boolean eliminarAlquiler(int idAlquila);
}


