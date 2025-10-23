package com.mycompany.videoclub.DAO.Interfaces;

import com.mycompany.videoclub.Modelos.Clientes;
import java.util.List;
import java.util.Optional;

public interface ICliente {

    boolean agregarCliente(Clientes c);                 
    boolean eliminarCliente(int idCliente);             
    boolean actualizarCliente(Clientes c);              
    Optional<Clientes> obtenerPorId(int idCliente);     
    List<Clientes> listarTodos();                       

    boolean existePorId(int idCliente);                
    Optional<Clientes> obtenerPorDni(String dni);
    
}

