package com.mycompany.videoclub.DAO;

import com.mycompany.videoclub.DAO.Interfaces.IAlquilan;
import com.mycompany.videoclub.Modelos.Alquila;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IAlquilanImpl implements IAlquilan {

    private static final String SQL_COPIA_DISPONIBLE =
            "SELECT Disponibilidad FROM COPIAS WHERE ID_Sku=?";
    private static final String SQL_RESERVAR_COPIA =
            "UPDATE COPIAS SET Disponibilidad=FALSE WHERE ID_Sku=? AND Disponibilidad=TRUE";
    private static final String SQL_LIBERAR_COPIA =
            "UPDATE COPIAS SET Disponibilidad=TRUE WHERE ID_Sku=?";
    private static final String SQL_EXISTE_ALQUILER_ACTIVO_POR_SKU =
            "SELECT 1 FROM ALQUILAN WHERE ID_Sku=? AND Fecha_devolucion_real IS NULL LIMIT 1";

    private static final String SQL_INSERT_ALQUILER =
            "INSERT INTO ALQUILAN (ID_Cliente, ID_Sku, Fecha_alquiler, Fecha_limite_devolucion, Fecha_devolucion_real) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT ID_Alquilan, ID_Cliente, ID_Sku, Fecha_alquiler, Fecha_limite_devolucion, Fecha_devolucion_real " +
            "FROM ALQUILAN WHERE ID_Alquilan=?";
    private static final String SQL_SELECT_ALL =
            "SELECT ID_Alquilan, ID_Cliente, ID_Sku, Fecha_alquiler, Fecha_limite_devolucion, Fecha_devolucion_real " +
            "FROM ALQUILAN ORDER BY ID_Alquilan DESC";
    private static final String SQL_SELECT_BY_CLIENTE =
            "SELECT ID_Alquilan, ID_Cliente, ID_Sku, Fecha_alquiler, Fecha_limite_devolucion, Fecha_devolucion_real " +
            "FROM ALQUILAN WHERE ID_Cliente=? ORDER BY ID_Alquilan DESC";
    // via COPIAS para filtrar por película
    private static final String SQL_SELECT_BY_PELICULA =
            "SELECT a.ID_Alquilan, a.ID_Cliente, a.ID_Sku, a.Fecha_alquiler, a.Fecha_limite_devolucion, a.Fecha_devolucion_real " +
            "FROM ALQUILAN a JOIN COPIAS c ON a.ID_Sku=c.ID_Sku " +
            "WHERE c.ID_Pelicula=? ORDER BY a.ID_Alquilan DESC";

    private static final String SQL_UPDATE_ALQUILER =
            "UPDATE ALQUILAN SET ID_Cliente=?, ID_Sku=?, Fecha_alquiler=?, Fecha_limite_devolucion=?, Fecha_devolucion_real=? " +
            "WHERE ID_Alquilan=?";
    private static final String SQL_SET_DEVOLUCION =
            "UPDATE ALQUILAN SET Fecha_devolucion_real=? WHERE ID_Alquilan=? AND Fecha_devolucion_real IS NULL";
    private static final String SQL_GET_SKU_FROM_ALQUILER_ACTIVO =
            "SELECT ID_Sku FROM ALQUILAN WHERE ID_Alquilan=? AND Fecha_devolucion_real IS NULL";
    private static final String SQL_DELETE_ALQUILER =
            "DELETE FROM ALQUILAN WHERE ID_Alquilan=?";

    private Alquila map(ResultSet rs) throws SQLException {
        return new Alquila(
                rs.getInt("ID_Alquilan"),
                rs.getInt("ID_Cliente"),
                rs.getInt("ID_Sku"),
                rs.getDate("Fecha_alquiler"),
                rs.getDate("Fecha_limite_devolucion"),
                rs.getDate("Fecha_devolucion_real")
        );
    }

    @Override
    public boolean agregarAlquiler(Alquila a) {
        BaseDatos base = new BaseDatos();
        try (Connection con = base.getConexion()) {
            if (con == null) return false;
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(SQL_COPIA_DISPONIBLE)) {
                ps.setInt(1, a.getIdSku());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { // no existe
                        System.out.println("⚠️ No existe la copia con ID_Sku=" + a.getIdSku());
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(SQL_EXISTE_ALQUILER_ACTIVO_POR_SKU)) {
                ps.setInt(1, a.getIdSku());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("⚠️ La copia ya está alquilada (alquiler activo).");
                        con.rollback();
                        return false;
                    }
                }
            }

            int updated;
            try (PreparedStatement ps = con.prepareStatement(SQL_RESERVAR_COPIA)) {
                ps.setInt(1, a.getIdSku());
                updated = ps.executeUpdate();
            }
            if (updated == 0) { // otra carrera la tomó antes
                System.out.println("⚠️ No se pudo reservar la copia (ya no está disponible).");
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_ALQUILER)) {
                ps.setInt(1, a.getIdCliente());
                ps.setInt(2, a.getIdSku());
                ps.setDate(3, a.getFechaAlquiler());
                ps.setDate(4, a.getFechaLimiteDevolucion());
                ps.setDate(5, a.getFechaDevolucionReal()); // puede ser NULL
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("Alquiler registrado: SKU=" + a.getIdSku() + " Cliente=" + a.getIdCliente());
            return true;

        } catch (SQLException e) {
            System.err.println("Error al agregar alquiler: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Alquila obtenerAlquiler(int idAlquila) {
        BaseDatos base = new BaseDatos();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idAlquila);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener alquiler: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Alquila> listarAlquileres() {
        BaseDatos base = new BaseDatos();
        List<Alquila> lista = new ArrayList<>();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));
            return lista;

        } catch (SQLException e) {
            System.err.println("Error al listar alquileres: " + e.getMessage());
            return lista;
        }
    }

    @Override
    public List<Alquila> listarPorCliente(int idCliente) {
        BaseDatos base = new BaseDatos();
        List<Alquila> lista = new ArrayList<>();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_CLIENTE)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Error al listar por cliente: " + e.getMessage());
            return lista;
        }
    }

    @Override
    public List<Alquila> listarPorPelicula(int idPelicula) {
        BaseDatos base = new BaseDatos();
        List<Alquila> lista = new ArrayList<>();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_PELICULA)) {

            ps.setInt(1, idPelicula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Error al listar por película: " + e.getMessage());
            return lista;
        }
    }

    @Override
    public boolean actualizarAlquiler(Alquila a) {
        BaseDatos base = new BaseDatos();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ALQUILER)) {

            ps.setInt(1, a.getIdCliente());
            ps.setInt(2, a.getIdSku());
            ps.setDate(3, a.getFechaAlquiler());
            ps.setDate(4, a.getFechaLimiteDevolucion());
            ps.setDate(5, a.getFechaDevolucionReal());
            ps.setInt(6, a.getIdAlquilan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar alquiler: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registrarDevolucion(int idAlquila, Date fechaDevolucionReal) {
        BaseDatos base = new BaseDatos();

        try (Connection con = base.getConexion()) {
            if (con == null) return false;
            con.setAutoCommit(false);

            // 1) obtener SKU del alquiler activo
            Integer sku = null;
            try (PreparedStatement ps = con.prepareStatement(SQL_GET_SKU_FROM_ALQUILER_ACTIVO)) {
                ps.setInt(1, idAlquila);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sku = rs.getInt(1);
                }
            }
            if (sku == null) {
                System.out.println("⚠️ No existe alquiler activo con ID=" + idAlquila);
                con.rollback();
                return false;
            }

            int upd;
            try (PreparedStatement ps = con.prepareStatement(SQL_SET_DEVOLUCION)) {
                ps.setDate(1, fechaDevolucionReal);
                ps.setInt(2, idAlquila);
                upd = ps.executeUpdate();
            }
            if (upd == 0) {
                System.out.println("⚠️ Ya estaba devuelto o no existe.");
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(SQL_LIBERAR_COPIA)) {
                ps.setInt(1, sku);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("✅ Devolución registrada. ID=" + idAlquila + ", SKU=" + sku);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar devolución: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarAlquiler(int idAlquila) {
        BaseDatos base = new BaseDatos();
        try (Connection con = base.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE_ALQUILER)) {

            ps.setInt(1, idAlquila);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar alquiler: " + e.getMessage());
            return false;
        }
    }
}
