
package com.mycompany.videoclub.DAO.Interfaces;

import com.mycompany.videoclub.Modelos.Copias;
import java.util.List;

public interface ICopia {

    boolean agregarCopia(Copias c);

    Copias obtenerCopia(int idSku);                        
    List<Copias> listarCopias();                          
    List<Copias> listarCopiasPorPelicula(int idPelicula);  
    List<Copias> listarDisponibles();                     
    List<Copias> listarNoDisponibles();                    

    boolean actualizarCopia(Copias c);                    
    boolean cambiarDisponibilidad(int idSku, boolean disponible);

    boolean eliminarCopia(int idSku);
}
