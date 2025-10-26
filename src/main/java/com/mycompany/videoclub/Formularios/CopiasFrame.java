package com.mycompany.videoclub.Formularios;

import com.mycompany.videoclub.DAO.ICopiasImpl;
import com.mycompany.videoclub.Modelos.Copias;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CopiasFrame extends JFrame {

    private final ICopiasImpl dao = new ICopiasImpl();

    private JTextField txtSku, txtIdPelicula, txtEstado, txtFiltrarIdPeli;
    private JCheckBox chkDisponible;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JButton btnAgregar, btnActualizar, btnEliminar, btnToggleDisp,
                    btnLimpiar, btnListar, btnListarDisp, btnListarNoDisp, btnFiltrarPorPeli;

    public CopiasFrame() {
        setTitle("Gestión de Copias");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSku = new JTextField();
        txtSku.setEditable(false);

        txtIdPelicula = new JTextField();
        chkDisponible = new JCheckBox("Disponible");
        txtEstado = new JTextField();       

        int fila = 0;
        addField(form, gbc, fila++, "ID_Sku:", txtSku);
        addField(form, gbc, fila++, "ID_Pelicula:", txtIdPelicula);
        addField(form, gbc, fila++, "Estado:", txtEstado);

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; form.add(new JLabel("Disponibilidad:"), gbc);
        gbc.gridx = 1; gbc.gridy = fila; gbc.weightx = 1; form.add(chkDisponible, gbc);
        fila++;

        add(form, BorderLayout.NORTH);

        String[] cols = {"ID_Sku", "ID_Pelicula", "Disponible", "Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int i = tabla.getSelectedRow();
                if (i >= 0) {
                    txtSku.setText(String.valueOf(modelo.getValueAt(i, 0)));
                    txtIdPelicula.setText(String.valueOf(modelo.getValueAt(i, 1)));
                    chkDisponible.setSelected(Boolean.parseBoolean(String.valueOf(modelo.getValueAt(i, 2))));
                    txtEstado.setText(String.valueOf(modelo.getValueAt(i, 3)));
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(10,10));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        txtFiltrarIdPeli = new JTextField(8);
        btnFiltrarPorPeli = new JButton("Filtrar por ID_Peli");
        filtro.add(new JLabel("ID_Peli:"));
        filtro.add(txtFiltrarIdPeli);
        filtro.add(btnFiltrarPorPeli);
        sur.add(filtro, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnAgregar      = new JButton("Agregar");
        btnActualizar   = new JButton("Actualizar");
        btnEliminar     = new JButton("Eliminar");
        btnToggleDisp   = new JButton("Cambiar Disp.");
        btnLimpiar      = new JButton("Limpiar");
        btnListar       = new JButton("Listar todas");
        btnListarDisp   = new JButton("Solo disponibles");
        btnListarNoDisp = new JButton("Solo NO disponibles");

        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnToggleDisp);
        botones.add(btnLimpiar);
        botones.add(btnListar);
        botones.add(btnListarDisp);
        botones.add(btnListarNoDisp);

        sur.add(botones, BorderLayout.CENTER);

        add(sur, BorderLayout.SOUTH);

        btnListar.addActionListener(e -> cargarTodas());
        btnListarDisp.addActionListener(e -> cargarDisponibles());
        btnListarNoDisp.addActionListener(e -> cargarNoDisponibles());
        btnFiltrarPorPeli.addActionListener(e -> cargarPorPelicula());

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnAgregar.addActionListener(e -> agregar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnToggleDisp.addActionListener(e -> toggleDisponibilidad());

        cargarTodas();
    }

    private void addField(JPanel p, GridBagConstraints gbc, int fila, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = fila; gbc.weightx = 1; p.add(comp, gbc);
    }

    private void cargarTabla(List<Copias> lista) {
        modelo.setRowCount(0);
        for (Copias c : lista) {
            modelo.addRow(new Object[]{
                    c.getIdSku(),
                    c.getIdPelicula(),
                    c.isDisponibilidad(),
                    c.getEstado()
            });
        }
        tabla.clearSelection();
    }

    private void cargarTodas() {
        cargarTabla(dao.listarCopias());
    }

    private void cargarDisponibles() {
        cargarTabla(dao.listarDisponibles());
    }

    private void cargarNoDisponibles() {
        cargarTabla(dao.listarNoDisponibles());
    }

    private void cargarPorPelicula() {
        String s = txtFiltrarIdPeli.getText().trim();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indica un ID_Pelicula para filtrar.");
            return;
        }
        try {
            int idPeli = Integer.parseInt(s);
            cargarTabla(dao.listarCopiasPorPelicula(idPeli));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID_Pelicula debe ser numérico.");
        }
    }

    private void limpiarFormulario() {
        txtSku.setText("");
        txtIdPelicula.setText("");
        chkDisponible.setSelected(true);
        txtEstado.setText("");
        tabla.clearSelection();
        txtIdPelicula.requestFocus();
    }

    private Copias copiaFromForm(boolean requireSku) {
        String idPeliStr = txtIdPelicula.getText().trim();
        String estado = txtEstado.getText().trim();
        boolean disp = chkDisponible.isSelected();

        if (idPeliStr.isEmpty() || estado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena ID_Pelicula y Estado.");
            return null;
        }

        int idPeli;
        try {
            idPeli = Integer.parseInt(idPeliStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID_Pelicula debe ser numérico.");
            return null;
        }

        Copias c = new Copias();
        c.setIdPelicula(idPeli);
        c.setEstado(estado);
        c.setDisponibilidad(disp);

        if (requireSku) {
            if (txtSku.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona una copia (falta ID_Sku).");
                return null;
            }
            try {
                c.setIdSku(Integer.parseInt(txtSku.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID_Sku inválido.");
                return null;
            }
        }
        return c;
    }

    private void agregar() {
        Copias c = copiaFromForm(false);
        if (c == null) return;

        if (dao.agregarCopia(c)) {
            JOptionPane.showMessageDialog(this, "Copia agregada.");
            cargarTodas();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        Copias c = copiaFromForm(true);
        if (c == null) return;

        if (dao.actualizarCopia(c)) {
            JOptionPane.showMessageDialog(this, "Copia actualizada.");
            cargarTodas();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtSku.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una copia en la tabla.");
            return;
        }
        int idSku;
        try {
            idSku = Integer.parseInt(txtSku.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID_Sku inválido.");
            return;
        }

        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar esta copia?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        if (dao.eliminarCopia(idSku)) {
            JOptionPane.showMessageDialog(this, "Copia eliminada.");
            cargarTodas();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleDisponibilidad() {
        if (txtSku.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una copia en la tabla.");
            return;
        }
        int idSku;
        try {
            idSku = Integer.parseInt(txtSku.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID_Sku inválido.");
            return;
        }

        boolean nuevaDisp = !chkDisponible.isSelected();
        if (dao.cambiarDisponibilidad(idSku, nuevaDisp)) {
            JOptionPane.showMessageDialog(this, "Disponibilidad actualizada.");
            cargarTodas();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar disponibilidad.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CopiasFrame().setVisible(true));
    }
}

