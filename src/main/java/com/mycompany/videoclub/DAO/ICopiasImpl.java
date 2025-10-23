package com.mycompany.videoclub.DAO;

import com.mycompany.videoclub.DAO.Interfaces.ICopia;
import com.mycompany.videoclub.Modelos.Copias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ICopiasImpl implements ICopia {

    private static final String SQL_INSERT =
            "INSERT INTO COPIAS (ID_Pelicula, Disponibilidad, Estado) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM COPIAS WHERE ID_Sku = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM COPIAS";
    private static final String SQL_SELECT_BY_PELICULA =
            "SELECT * FROM COPIAS WHERE ID_Pelicula = ?";
    private static final String SQL_SELECT_DISPONIBLES =
            "SELECT * FROM COPIAS WHERE Disponibilidad = TRUE";
    private static final String SQL_SELECT_NO_DISPONIBLES =
            "SELECT * FROM COPIAS WHERE Disponibilidad = FALSE";
    private static final String SQL_UPDATE =
            "UPDATE COPIAS SET ID_Pelicula = ?, Disponibilidad = ?, Estado = ? WHERE ID_Sku = ?";
    private static final String SQL_UPDATE_DISPONIBILIDAD =
            "UPDATE COPIAS SET Disponibilidad = ? WHERE ID_Sku = ?";
    private static final String SQL_DELETE =
            "DELETE FROM COPIAS WHERE ID_Sku = ?";


    private Copias map(ResultSet rs) throws SQLException {
        Copias c = new Copias();
        c.setIdSku(rs.getInt("ID_Sku"));
        c.setIdPelicula(rs.getInt("ID_Pelicula"));
        c.setDisponibilidad(rs.getBoolean("Disponibilidad"));
        c.setEstado(rs.getString("Estado"));
        return c;
    }

    @Override
    public boolean agregarCopia(Copias c) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, c.getIdPelicula());
            ps.setBoolean(2, c.isDisponibilidad());
            ps.setString(3, c.getEstado());

            ps.executeUpdate();
            System.out.println("Copia agregada correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al agregar copia: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Copias obtenerCopia(int idSku) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idSku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener copia: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Copias> listarCopias() {
        BaseDatos base = new BaseDatos();
        List<Copias> lista = new ArrayList<>();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar copias: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Copias> listarCopiasPorPelicula(int idPelicula) {
        BaseDatos base = new BaseDatos();
        List<Copias> lista = new ArrayList<>();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_PELICULA)) {

            ps.setInt(1, idPelicula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar copias por película: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Copias> listarDisponibles() {
        BaseDatos base = new BaseDatos();
        List<Copias> lista = new ArrayList<>();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_DISPONIBLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar copias disponibles: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Copias> listarNoDisponibles() {
        BaseDatos base = new BaseDatos();
        List<Copias> lista = new ArrayList<>();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_NO_DISPONIBLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar copias no disponibles: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizarCopia(Copias c) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, c.getIdPelicula());
            ps.setBoolean(2, c.isDisponibilidad());
            ps.setString(3, c.getEstado());
            ps.setInt(4, c.getIdSku());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar copia: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarDisponibilidad(int idSku, boolean disponible) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_DISPONIBILIDAD)) {

            ps.setBoolean(1, disponible);
            ps.setInt(2, idSku);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar disponibilidad: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarCopia(int idSku) {
        BaseDatos base = new BaseDatos();
        try (Connection conn = base.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idSku);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar copia: " + e.getMessage());
            return false;
        }
    }
}

