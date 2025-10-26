package com.mycompany.videoclub.Formularios;

import javax.swing.*;

public class MenuPrincipal extends JFrame {

    private JButton btnClientes, btnPeliculas, btnCopias, btnAlquileres, btnSalir;

    public MenuPrincipal() {
        setTitle("Videoclub - Menú principal");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // ====== Botones ======
        btnClientes   = new JButton("Clientes");
        btnPeliculas  = new JButton("Películas");
        btnCopias     = new JButton("Copias");
        btnAlquileres = new JButton("Alquileres");
        btnSalir      = new JButton("Salir");

        // ====== Posiciones ======
        btnClientes.setBounds(40, 40, 150, 40);
        btnPeliculas.setBounds(220, 40, 150, 40);
        btnCopias.setBounds(40, 100, 150, 40);
        btnAlquileres.setBounds(220, 100, 150, 40);
        btnSalir.setBounds(130, 180, 150, 40);

        // ====== Añadir ======
        add(btnClientes);
        add(btnPeliculas);
        add(btnCopias);
        add(btnAlquileres);
        add(btnSalir);

        // ====== Acciones ======
        btnClientes.addActionListener(e -> new FormClientes().setVisible(true));
        btnPeliculas.addActionListener(e -> new PeliculasFrame().setVisible(true));
        btnCopias.addActionListener(e -> new CopiasFrame().setVisible(true));
        btnAlquileres.addActionListener(e -> new AlquileresFrame().setVisible(true));
        btnSalir.addActionListener(e -> dispose());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

