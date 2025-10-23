package com.mycompany.videoclub.DAO;

import com.mycompany.videoclub.DAO.Interfaces.IPelicula;
import com.mycompany.videoclub.Modelos.Peliculas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IPeliculaImpl implements IPelicula {

    private static final String SQL_INSERT =
            "INSERT INTO PELICULAS (Titulo, Director, Anio_lanzamiento, Genero, Cantidad) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DELETE =
            "DELETE FROM PELICULAS WHERE ID_Pelicula = ?";

    private static final String SQL_UPDATE =
            "UPDATE PELICULAS SET Titulo = ?, Director = ?, Anio_lanzamiento = ?, Genero = ?, Cantidad = ? WHERE ID_Pelicula = ?";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM PELICULAS WHERE ID_Pelicula = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM PELICULAS";


    private Peliculas map(ResultSet rs) throws SQLException {
        return new Peliculas(
                rs.getInt("ID_Pelicula"),
                rs.getString("Titulo"),
                rs.getString("Director"),
                rs.getInt("Anio_lanzamiento"),
                rs.getString("Genero"),
                rs.getInt("Cantidad")
        );
    }


    @Override
    public boolean agregarPelicula(Peliculas p) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAnioLanzamiento());
            ps.setString(4, p.getGenero());
            ps.setInt(5, p.getCantidad());

            ps.executeUpdate();
            System.out.println("Película agregada correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al agregar película: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPelicula(int idPelicula) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idPelicula);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Película eliminada correctamente.");
                return true;
            } else {
                System.out.println(" No se encontró ninguna película con ID=" + idPelicula);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar película: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarPelicula(Peliculas p) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAnioLanzamiento());
            ps.setString(4, p.getGenero());
            ps.setInt(5, p.getCantidad());
            ps.setInt(6, p.getIdPelicula());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar película: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Peliculas obtenerPelicula(int idPelicula) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idPelicula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener película: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Peliculas> listarPelicula() {
        BaseDatos base = new BaseDatos();
        List<Peliculas> lista = new ArrayList<>();

        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar películas: " + e.getMessage());
        }
        return lista;
    }
}

