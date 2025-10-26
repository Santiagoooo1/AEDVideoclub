package com.mycompany.videoclub.DAO;

import com.mycompany.videoclub.DAO.Interfaces.IPelicula;
import com.mycompany.videoclub.Modelos.Peliculas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    // Extra para el flujo con copias
    private static final String SQL_INSERT_COPIA =
        "INSERT INTO COPIAS (ID_Pelicula, Disponibilidad, Estado) VALUES (?, TRUE, 'Buen estado')";
    private static final String SQL_DELETE_COPIAS_BY_PELI =
        "DELETE FROM COPIAS WHERE ID_Pelicula = ?";

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

    // ⚡ NUEVO: inserta película y crea N copias en una sola transacción
    public boolean agregarPeliculaConCopias(Peliculas p) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int idPelicula;
            // 1) Insertar película recuperando ID
            try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getTitulo());
                ps.setString(2, p.getDirector());
                ps.setInt(3, p.getAnioLanzamiento());
                ps.setString(4, p.getGenero());
                ps.setInt(5, p.getCantidad());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.err.println("No se obtuvo ID_Pelicula generado.");
                        return false;
                    }
                    idPelicula = rs.getInt(1);
                }
            }

            // 2) Crear N copias
            try (PreparedStatement psCopia = conn.prepareStatement(SQL_INSERT_COPIA)) {
                for (int i = 0; i < p.getCantidad(); i++) {
                    psCopia.setInt(1, idPelicula);
                    psCopia.addBatch();
                }
                psCopia.executeBatch();
            }

            conn.commit();
            System.out.println("Película (ID=" + idPelicula + ") y " + p.getCantidad() + " copias creadas.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar película/copias: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPelicula(int idPelicula) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion()) {
            if (conn == null) return false;

            // Si tu FK es RESTRICT, borra primero las copias y luego la película, en transacción
            conn.setAutoCommit(false);

            try (PreparedStatement psDelCopias = conn.prepareStatement(SQL_DELETE_COPIAS_BY_PELI)) {
                psDelCopias.setInt(1, idPelicula);
                psDelCopias.executeUpdate();
            }

            int filas;
            try (PreparedStatement psDelPeli = conn.prepareStatement(SQL_DELETE)) {
                psDelPeli.setInt(1, idPelicula);
                filas = psDelPeli.executeUpdate();
            }

            conn.commit();
            if (filas > 0) {
                System.out.println("Película y copias eliminadas.");
                return true;
            } else {
                System.out.println("No se encontró película con ID=" + idPelicula);
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
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener película: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Peliculas> listarPelicula() { // si quieres: renombra a listarPeliculas()
        BaseDatos base = new BaseDatos();
        List<Peliculas> lista = new ArrayList<>();

        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar películas: " + e.getMessage());
        }
        return lista;
    }
}


