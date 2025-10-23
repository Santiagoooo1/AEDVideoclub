package com.mycompany.videoclub.DAO;

import com.mycompany.videoclub.DAO.BaseDatos;
import com.mycompany.videoclub.DAO.Interfaces.ICliente;
import com.mycompany.videoclub.Modelos.Clientes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.util.ArrayList;

public class IClienteImpl implements ICliente {

    private static String categoriaToDb(Clientes.Categoria c) {
        return (c == Clientes.Categoria.ORO) ? "Oro" : "Normal";
    }

    private static Clientes.Categoria categoriaFromDb(String s) {
        return "Oro".equalsIgnoreCase(s) ? Clientes.Categoria.ORO : Clientes.Categoria.NORMAL;
    }

    @Override
    public boolean agregarCliente(Clientes c) {
        BaseDatos base = new BaseDatos();
        Connection conn = base.getConexion();

        String sql = "INSERT INTO CLIENTES (DNI, Nombre, Apellidos, Direccion, Telefono, Categoria) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getDni());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellidos());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getTelefono());

            // usar mapeo (y valor por defecto si viene null)
            String categoriaDb = (c.getCategoria() == null)
                    ? "Normal"
                    : categoriaToDb(c.getCategoria());
            ps.setString(6, categoriaDb);

            ps.executeUpdate();
            System.out.println("El cliente se ha agregado correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;

        } finally {
            base.cerrarConexion();
        }

    }

    @Override
    public boolean eliminarCliente(int idCliente) {
        BaseDatos base = new BaseDatos();
        String sql = "DELETE FROM CLIENTES WHERE ID_Cliente = ?";

        try (Connection conn = base.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Cliente eliminado (ID=" + idCliente + ").");
                return true;
            } else {
                System.out.println("No existe cliente con ID=" + idCliente + ".");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        } finally {
            base.cerrarConexion();
        }
    }

    @Override
    public boolean actualizarCliente(Clientes c) {
            BaseDatos base= new BaseDatos ();
            String sql="UPDATE SET CLIENTES DNI=?, Nombre=?, Apellidos=?, Direccion=?, Telefono=?, Categoria=?" + 
                    "WHERE ID_Cliente=?";
            
            try(Connection conn=base.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, c.getDni());
                ps.setString(2, c.getNombre());
                ps.setString(1, c.getApellidos());
                ps.setString(4, c.getDireccion());
                ps.setString(5, c.getTelefono());
                ps.setString(6, (c.getCategoria()==null) ? "Normal" : categoriaToDb(c.getCategoria()));
                ps.setInt(7, c.getIdCliente());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error al actualizar cliente: " + e.getMessage());
        return false;
    } finally {
        base.cerrarConexion();
    }
                
      
                    
    }

    @Override
public Optional<Clientes> obtenerPorId(int idCliente) {
    BaseDatos base = new BaseDatos();
    String sql = "SELECT ID_Cliente, DNI, Nombre, Apellidos, Direccion, Telefono, Categoria " +
                 "FROM CLIENTES WHERE ID_Cliente=?";

    try (Connection conn = base.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Clientes c = new Clientes(
                    rs.getInt("ID_Cliente"),
                    rs.getString("DNI"),
                    rs.getString("Nombre"),
                    rs.getString("Apellidos"),
                    rs.getString("Direccion"),
                    rs.getString("Telefono"),
                    categoriaFromDb(rs.getString("Categoria"))
                );
                return Optional.of(c);
            }
        }
        return Optional.empty();

    } catch (SQLException e) {
        System.err.println("Error al obtener cliente por ID: " + e.getMessage());
        return Optional.empty();
    } finally {
        base.cerrarConexion();
    }
}


    @Override
    public List<Clientes> listarTodos() {
    BaseDatos base = new BaseDatos();
    String sql = "SELECT ID_Cliente, DNI, Nombre, Apellidos, Direccion, Telefono, Categoria FROM CLIENTES";
    List<Clientes> lista = new ArrayList<>();

    try (Connection conn = base.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(new Clientes(
                rs.getInt("ID_Cliente"),
                rs.getString("DNI"),
                rs.getString("Nombre"),
                rs.getString("Apellidos"),
                rs.getString("Direccion"),
                rs.getString("Telefono"),
                categoriaFromDb(rs.getString("Categoria"))
            ));
        }
        return lista;

    } catch (SQLException e) {
        System.err.println("Error al listar clientes: " + e.getMessage());
        return lista;
    } finally {
        base.cerrarConexion();
    }
}

    @Override
    public boolean existePorId(int idCliente) {
    BaseDatos base = new BaseDatos();
    String sql = "SELECT 1 FROM CLIENTES WHERE ID_Cliente=?";

    try (Connection conn = base.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }

    } catch (SQLException e) {
        System.err.println("Error al verificar existencia por ID: " + e.getMessage());
        return false;
    } finally {
        base.cerrarConexion();
    }
}

    @Override
    public Optional<Clientes> obtenerPorDni(String dni) {
    BaseDatos base = new BaseDatos();
    String sql = "SELECT ID_Cliente, DNI, Nombre, Apellidos, Direccion, Telefono, Categoria " +
                 "FROM CLIENTES WHERE DNI=?";

    try (Connection conn = base.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, dni);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Clientes c = new Clientes(
                    rs.getInt("ID_Cliente"),
                    rs.getString("DNI"),
                    rs.getString("Nombre"),
                    rs.getString("Apellidos"),
                    rs.getString("Direccion"),
                    rs.getString("Telefono"),
                    categoriaFromDb(rs.getString("Categoria"))
                );
                return Optional.of(c);
            }
        }
        return Optional.empty();

    } catch (SQLException e) {
        System.err.println("Error al obtener cliente por DNI: " + e.getMessage());
        return Optional.empty();
    } finally {
        base.cerrarConexion();
    }
}

}
