package com.mycompany.videoclub;

import com.mycompany.videoclub.DAO.IPeliculaImpl;
import com.mycompany.videoclub.DAO.ICopiasImpl;
import com.mycompany.videoclub.DAO.IClienteImpl;
import com.mycompany.videoclub.Modelos.Peliculas;
import com.mycompany.videoclub.Modelos.Clientes;

public class MainTest {
    public static void main(String[] args) {

        // 1️⃣ Insertar película + crear copias
        var daoPeli = new IPeliculaImpl();
        var peli = new Peliculas("Blade Runner", "Ridley Scott", 1982, "Sci-Fi", 3);
        boolean okPeli = daoPeli.agregarPeliculaConCopias(peli);
        System.out.println("agregarPeliculaConCopias => " + okPeli);

        // 2️⃣ Listar películas
        System.out.println("Películas:");
        daoPeli.listarPelicula().forEach(System.out::println);

        // 3️⃣ Insertar cliente
        var daoCli = new IClienteImpl();
        var cli = new Clientes("12345678Z", "Ana", "López", "C/ Mayor 1", "600111222", Clientes.Categoria.ORO);
        boolean okCli = daoCli.agregarCliente(cli);
        System.out.println("agregarCliente => " + okCli);

        // 4️⃣ Mostrar copias de la película
        var daoCopias = new ICopiasImpl();
        var peliId = daoPeli.listarPelicula().stream()
                .filter(p -> p.getTitulo().equalsIgnoreCase("Blade Runner")
                        && p.getDirector().equalsIgnoreCase("Ridley Scott")
                        && p.getAnioLanzamiento() == 1982)
                .map(Peliculas::getIdPelicula)
                .findFirst()
                .orElse(null);

        if (peliId != null) {
            System.out.println("Copias de Blade Runner:");
            daoCopias.listarCopiasPorPelicula(peliId).forEach(System.out::println);
        }
    }
}
