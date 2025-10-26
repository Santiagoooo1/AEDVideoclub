package com.mycompany.videoclub.Formularios;

import com.mycompany.videoclub.DAO.IPeliculaImpl;
import com.mycompany.videoclub.Modelos.Peliculas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PeliculasFrame extends JFrame {

    // DAO
    private final IPeliculaImpl dao = new IPeliculaImpl();

    // Campos
    private JTextField txtId, txtTitulo, txtDirector, txtAnio, txtGenero, txtCantidad;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // Botones
    private JButton btnAgregarConCopias, btnActualizar, btnEliminar, btnLimpiar, btnListar;

    public PeliculasFrame() {
        setTitle("Gestión de Películas");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ====== Panel superior: Formulario ======
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);

        txtTitulo = new JTextField();
        txtDirector = new JTextField();
        txtAnio = new JTextField();
        txtGenero = new JTextField();
        txtCantidad = new JTextField();

        int fila = 0;

        addField(form, gbc, fila++, "ID:", txtId);
        addField(form, gbc, fila++, "Título:", txtTitulo);
        addField(form, gbc, fila++, "Director:", txtDirector);
        addField(form, gbc, fila++, "Año lanzamiento:", txtAnio);
        addField(form, gbc, fila++, "Género:", txtGenero);
        addField(form, gbc, fila++, "Cantidad (copias):", txtCantidad);

        add(form, BorderLayout.NORTH);

        // ====== Panel central: Tabla ======
        String[] cols = {"ID_Pelicula", "Título", "Director", "Año", "Género", "Cantidad"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Al hacer clic sobre una fila, pasar los datos al formulario
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = tabla.getSelectedRow();
                if (i >= 0) {
                    txtId.setText(String.valueOf(modelo.getValueAt(i, 0)));
                    txtTitulo.setText(String.valueOf(modelo.getValueAt(i, 1)));
                    txtDirector.setText(String.valueOf(modelo.getValueAt(i, 2)));
                    txtAnio.setText(String.valueOf(modelo.getValueAt(i, 3)));
                    txtGenero.setText(String.valueOf(modelo.getValueAt(i, 4)));
                    txtCantidad.setText(String.valueOf(modelo.getValueAt(i, 5)));
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ====== Panel inferior: Botonera ======
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnAgregarConCopias = new JButton("Agregar + Copias");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnListar = new JButton("Listar todo");

        botones.add(btnAgregarConCopias);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnListar);

        add(botones, BorderLayout.SOUTH);

        // ====== Listeners ======
        btnListar.addActionListener(e -> cargarTabla());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnAgregarConCopias.addActionListener(e -> agregarPeliculaConCopias());
        btnActualizar.addActionListener(e -> actualizarPelicula());
        btnEliminar.addActionListener(e -> eliminarPelicula());

        // Primera carga
        cargarTabla();
    }

    private void addField(JPanel p, GridBagConstraints gbc, int fila, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = fila; gbc.weightx = 1; p.add(comp, gbc);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        List<Peliculas> lista = dao.listarPelicula();
        for (Peliculas p : lista) {
            modelo.addRow(new Object[]{
                    p.getIdPelicula(),
                    p.getTitulo(),
                    p.getDirector(),
                    p.getAnioLanzamiento(),
                    p.getGenero(),
                    p.getCantidad()
            });
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtTitulo.setText("");
        txtDirector.setText("");
        txtAnio.setText("");
        txtGenero.setText("");
        txtCantidad.setText("");
        tabla.clearSelection();
        txtTitulo.requestFocus();
    }

    private Peliculas peliculaFromForm(boolean requireId) {
        String titulo = txtTitulo.getText().trim();
        String director = txtDirector.getText().trim();
        String anioStr = txtAnio.getText().trim();
        String genero = txtGenero.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();

        if (titulo.isEmpty() || director.isEmpty() || anioStr.isEmpty() ||
            genero.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int anio;
        int cantidad;
        try {
            anio = Integer.parseInt(anioStr);
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Año y Cantidad deben ser numéricos.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Peliculas p = new Peliculas();
        p.setTitulo(titulo);
        p.setDirector(director);
        p.setAnioLanzamiento(anio);
        p.setGenero(genero);
        p.setCantidad(cantidad);

        if (requireId) {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla (falta ID).",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            try {
                p.setIdPelicula(Integer.parseInt(txtId.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return p;
    }

    private void agregarPeliculaConCopias() {
        Peliculas p = peliculaFromForm(false);
        if (p == null) return;

        boolean ok = dao.agregarPeliculaConCopias(p);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Película y copias creadas correctamente.");
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear la película/copias.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarPelicula() {
        Peliculas p = peliculaFromForm(true);
        if (p == null) return;

        boolean ok = dao.actualizarPelicula(p);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Película actualizada.");
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPelicula() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una película (ID) en la tabla.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar película y sus copias?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = dao.eliminarPelicula(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Película y copias eliminadas.");
            cargarTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PeliculasFrame().setVisible(true));
    }
}

