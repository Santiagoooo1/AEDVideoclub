package com.mycompany.videoclub.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDatos {
    
    private Connection conn;
    
    private static final String URL = "jdbc:mariadb://localhost:3306/videoclub";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public BaseDatos() {
        conectar();
    }
    
    public void conectar() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida correctamente con la base de datos 'videoclub'.");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de MariaDB: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        return conn;
    }

    public void cerrarConexion() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        BaseDatos bd = new BaseDatos();
        bd.cerrarConexion();
    }
}




