package com.mycompany.videoclub.Formularios;

import com.mycompany.videoclub.DAO.IClienteImpl;
import com.mycompany.videoclub.Modelos.Clientes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class FormClientes extends JFrame {

    private final IClienteImpl dao = new IClienteImpl();

    // Campos
    private JTextField txtId, txtDni, txtNombre, txtApellidos, txtDireccion, txtTelefono, txtBuscarDni;
    private JComboBox<String> cbCategoria;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // Botones
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnListar, btnBuscarDni;

    public FormClientes() {
        setTitle("Gestión de Clientes");
        setSize(920, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirUI();
        cargarTabla(dao.listarTodos());
        eventos();
    }

    private void construirUI() {
        // Panel datos
        JPanel pDatos = new JPanel(new GridLayout(4, 4, 8, 8));
        pDatos.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));

        txtId = new JTextField(); txtId.setEditable(false);
        txtDni = new JTextField();
        txtNombre = new JTextField();
        txtApellidos = new JTextField();
        txtDireccion = new JTextField();
        txtTelefono = new JTextField();
        cbCategoria = new JComboBox<>(new String[]{"Normal", "Oro"});

        pDatos.add(new JLabel("ID:"));            pDatos.add(txtId);
        pDatos.add(new JLabel("DNI:"));           pDatos.add(txtDni);
        pDatos.add(new JLabel("Nombre:"));        pDatos.add(txtNombre);
        pDatos.add(new JLabel("Apellidos:"));     pDatos.add(txtApellidos);
        pDatos.add(new JLabel("Dirección:"));     pDatos.add(txtDireccion);
        pDatos.add(new JLabel("Teléfono:"));      pDatos.add(txtTelefono);
        pDatos.add(new JLabel("Categoría:"));     pDatos.add(cbCategoria);

        // Panel búsqueda
        JPanel pBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBuscar.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        txtBuscarDni = new JTextField(16);
        btnBuscarDni = new JButton("Buscar por DNI");
        pBuscar.add(new JLabel("DNI:"));
        pBuscar.add(txtBuscarDni);
        pBuscar.add(btnBuscarDni);

        // Tabla
        modelo = new DefaultTableModel(
                new Object[]{"ID", "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Categoría"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tabla);

        // Botones CRUD
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnListar = new JButton("Listar todos");

        pBtns.add(btnAgregar);
        pBtns.add(btnActualizar);
        pBtns.add(btnEliminar);
        pBtns.add(btnLimpiar);
        pBtns.add(btnListar);

        // Layout general
        setLayout(new BorderLayout(8, 8));
        JPanel norte = new JPanel(new BorderLayout(8, 8));
        norte.add(pDatos, BorderLayout.CENTER);
        norte.add(pBuscar, BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(pBtns, BorderLayout.SOUTH);
    }

    private void eventos() {
        // Cargar selección a formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int r = tabla.getSelectedRow();
                txtId.setText(Objects.toString(modelo.getValueAt(r, 0), ""));
                txtDni.setText(Objects.toString(modelo.getValueAt(r, 1), ""));
                txtNombre.setText(Objects.toString(modelo.getValueAt(r, 2), ""));
                txtApellidos.setText(Objects.toString(modelo.getValueAt(r, 3), ""));
                txtDireccion.setText(Objects.toString(modelo.getValueAt(r, 4), ""));
                txtTelefono.setText(Objects.toString(modelo.getValueAt(r, 5), ""));
                cbCategoria.setSelectedItem(Objects.toString(modelo.getValueAt(r, 6), "Normal"));
            }
        });

        // Agregar
        btnAgregar.addActionListener(e -> {
            Clientes c = leerFormulario(false);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Completa DNI, Nombre, Apellidos, Dirección, Teléfono.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = dao.agregarCliente(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cliente agregado.");
                cargarTabla(dao.listarTodos());
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Actualizar
        btnActualizar.addActionListener(e -> {
            Clientes c = leerFormulario(true);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla y corrige los datos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = dao.actualizarCliente(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado.");
                cargarTabla(dao.listarTodos());
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Eliminar
        btnEliminar.addActionListener(e -> {
            if (txtId.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = Integer.parseInt(txtId.getText().trim());
            int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente ID=" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                boolean ok = dao.eliminarCliente(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                    cargarTabla(dao.listarTodos());
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar (¿tiene alquileres?).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Limpiar formulario
        btnLimpiar.addActionListener(e -> limpiar());

        // Listar todos
        btnListar.addActionListener(e -> cargarTabla(dao.listarTodos()));

        // Buscar por DNI
        btnBuscarDni.addActionListener(e -> {
            String dni = txtBuscarDni.getText().trim();
            if (dni.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce un DNI.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            dao.obtenerPorDni(dni).ifPresentOrElse(
                    c -> {
                        cargarTabla(java.util.List.of(c));
                        // además lo cargo al formulario
                        txtId.setText(String.valueOf(c.getIdCliente()));
                        txtDni.setText(c.getDni());
                        txtNombre.setText(c.getNombre());
                        txtApellidos.setText(c.getApellidos());
                        txtDireccion.setText(c.getDireccion());
                        txtTelefono.setText(c.getTelefono());
                        cbCategoria.setSelectedItem(c.getCategoria() == Clientes.Categoria.ORO ? "Oro" : "Normal");
                    },
                    () -> {
                        JOptionPane.showMessageDialog(this, "No se encontró el DNI: " + dni);
                        cargarTabla(java.util.List.of());
                    }
            );
        });
    }

    private void cargarTabla(List<Clientes> lista) {
        modelo.setRowCount(0);
        for (Clientes c : lista) {
            modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getDni(),
                    c.getNombre(),
                    c.getApellidos(),
                    c.getDireccion(),
                    c.getTelefono(),
                    (c.getCategoria() == Clientes.Categoria.ORO) ? "Oro" : "Normal"
            });
        }
    }

    private Clientes leerFormulario(boolean exigeId) {
        try {
            Integer id = null;
            if (exigeId) {
                if (txtId.getText().isBlank()) return null;
                id = Integer.parseInt(txtId.getText().trim());
            }
            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String cat = Objects.toString(cbCategoria.getSelectedItem(), "Normal");

            if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty() || telefono.isEmpty())
                return null;

            Clientes.Categoria categoria = "Oro".equalsIgnoreCase(cat) ? Clientes.Categoria.ORO : Clientes.Categoria.NORMAL;
            Clientes c = new Clientes(dni, nombre, apellidos, direccion, telefono, categoria);
            if (id != null) c.setIdCliente(id);
            return c;
        } catch (Exception ex) {
            return null;
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtDni.setText("");
        txtNombre.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        cbCategoria.setSelectedIndex(0);
        tabla.clearSelection();
    }

    // Para probar solo esta ventana:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormClientes().setVisible(true));
    }
}
