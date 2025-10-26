package com.mycompany.videoclub.Formularios;

import com.mycompany.videoclub.DAO.IAlquilanImpl;
import com.mycompany.videoclub.DAO.IClienteImpl;
import com.mycompany.videoclub.DAO.ICopiasImpl;
import com.mycompany.videoclub.DAO.IPeliculaImpl;
import com.mycompany.videoclub.Modelos.Alquila;
import com.mycompany.videoclub.Modelos.Clientes;
import com.mycompany.videoclub.Modelos.Copias;
import com.mycompany.videoclub.Modelos.Peliculas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AlquileresFrame extends JFrame {

    private final IAlquilanImpl daoAlq = new IAlquilanImpl();
    private final IClienteImpl  daoCli = new IClienteImpl();
    private final ICopiasImpl   daoCop = new ICopiasImpl();
    private final IPeliculaImpl daoPel = new IPeliculaImpl();

    // Top: selección para registrar alquiler
    private JComboBox<Clientes> cbClientes;
    private JComboBox<Copias>   cbCopias;       // solo disponibles
    private JSpinner spFechaAlq, spFechaLim;
    private JButton btnAlquilar;

    // Filtros
    private JComboBox<Clientes> cbFiltroCliente;
    private JComboBox<Peliculas> cbFiltroPelicula;
    private JCheckBox chkTodos;
    private JButton btnRefrescar, btnFiltrarCliente, btnFiltrarPelicula, btnListarActivos, btnListarTodos;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // Acciones sobre selección
    private JButton btnDevolver, btnEliminar;

    public AlquileresFrame() {
        setTitle("Gestión de Alquileres");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        construirUI();
        cargarCombosAltas();
        cargarCombosFiltros();
        cargarActivos();
        wireEvents();
    }

    private void construirUI() {
        // ===== Panel superior: Registrar alquiler =====
        JPanel pAlta = new JPanel(new GridBagLayout());
        pAlta.setBorder(BorderFactory.createTitledBorder("Registrar alquiler"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbClientes = new JComboBox<>();
        cbCopias   = new JComboBox<>();
        spFechaAlq = new JSpinner(new SpinnerDateModel());
        spFechaLim = new JSpinner(new SpinnerDateModel());
        spFechaAlq.setEditor(new JSpinner.DateEditor(spFechaAlq, "yyyy-MM-dd"));
        spFechaLim.setEditor(new JSpinner.DateEditor(spFechaLim, "yyyy-MM-dd"));

        // valores por defecto: hoy y hoy+3
        Calendar cal = Calendar.getInstance();
        spFechaAlq.setValue(cal.getTime());
        cal.add(Calendar.DATE, 3);
        spFechaLim.setValue(cal.getTime());

        int r=0;
        addField(pAlta, gbc, r++, "Cliente:", cbClientes);
        addField(pAlta, gbc, r++, "Copia disponible:", cbCopias);
        addField(pAlta, gbc, r++, "Fecha alquiler:", spFechaAlq);
        addField(pAlta, gbc, r++, "Fecha límite:", spFechaLim);

        btnAlquilar = new JButton("Registrar alquiler");
        gbc.gridx=1; gbc.gridy=r; gbc.weightx=1; pAlta.add(btnAlquilar, gbc);

        // ===== Panel medio-izq: Filtros =====
        JPanel pFiltros = new JPanel(new GridBagLayout());
        pFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(4,4,4,4);
        g2.fill = GridBagConstraints.HORIZONTAL;

        cbFiltroCliente  = new JComboBox<>();
        cbFiltroPelicula = new JComboBox<>();
        btnFiltrarCliente  = new JButton("Por cliente");
        btnFiltrarPelicula = new JButton("Por película");
        btnListarActivos   = new JButton("Solo activos");
        btnListarTodos     = new JButton("Todos");
        chkTodos = new JCheckBox("Mostrar devueltos (al marcar 'Todos')");

        int f=0;
        addField(pFiltros, g2, f++, "Cliente:", cbFiltroCliente);
        g2.gridx=1; g2.gridy=f-1; pFiltros.add(btnFiltrarCliente, g2);
        addField(pFiltros, g2, f++, "Película:", cbFiltroPelicula);
        g2.gridx=1; g2.gridy=f-1; pFiltros.add(btnFiltrarPelicula, g2);

        g2.gridx=0; g2.gridy=f++; g2.gridwidth=2; pFiltros.add(chkTodos, g2);
        JPanel pList = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        pList.add(btnListarActivos);
        pList.add(btnListarTodos);
        g2.gridx=0; g2.gridy=f++; g2.gridwidth=2; pFiltros.add(pList, g2);

        btnRefrescar = new JButton("Refrescar combos");
        g2.gridx=0; g2.gridy=f; g2.gridwidth=2; pFiltros.add(btnRefrescar, g2);

        // ===== Panel superior combinado =====
        JPanel norte = new JPanel(new BorderLayout(10,10));
        norte.add(pAlta, BorderLayout.CENTER);
        norte.add(pFiltros, BorderLayout.EAST);
        add(norte, BorderLayout.NORTH);

        // ===== Tabla =====
        String[] cols = {"ID_Alquilan","ID_Cliente","Cliente","ID_Sku","Película",
                "Fecha_alquiler","Fecha_límite","Fecha_devolución"};
        modelo = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r1, int c1) { return false; } };
        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ===== Panel inferior: acciones sobre selección =====
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnDevolver = new JButton("Registrar devolución");
        btnEliminar = new JButton("Eliminar alquiler");
        sur.add(btnDevolver);
        sur.add(btnEliminar);
        add(sur, BorderLayout.SOUTH);
    }

    private void addField(JPanel p, GridBagConstraints gbc, int fila, String label, JComponent comp) {
        gbc.gridx=0; gbc.gridy=fila; gbc.weightx=0; p.add(new JLabel(label), gbc);
        gbc.gridx=1; gbc.gridy=fila; gbc.weightx=1; p.add(comp, gbc);
    }

    // ==== Cargas iniciales ====
    private void cargarCombosAltas() {
        cbClientes.removeAllItems();
        for (Clientes c : daoCli.listarTodos()) cbClientes.addItem(c);

        cbCopias.removeAllItems();
        for (Copias c : daoCop.listarDisponibles()) cbCopias.addItem(c);
    }

    private void cargarCombosFiltros() {
        cbFiltroCliente.removeAllItems();
        cbFiltroCliente.addItem(null); // opción vacía
        for (Clientes c : daoCli.listarTodos()) cbFiltroCliente.addItem(c);

        cbFiltroPelicula.removeAllItems();
        cbFiltroPelicula.addItem(null);
        for (Peliculas p : daoPel.listarPelicula()) cbFiltroPelicula.addItem(p);
    }

    private void cargarActivos() {
        modelo.setRowCount(0);
        for (Alquila a : daoAlq.listarAlquileres()) {
            if (a.getFechaDevolucionReal() != null) continue;
            addRow(a);
        }
    }

    private void cargarTodos() {
        modelo.setRowCount(0);
        for (Alquila a : daoAlq.listarAlquileres()) addRow(a);
    }

    private void addRow(Alquila a) {
        String cliente = daoCli.obtenerPorId(a.getIdCliente())
                .map(c -> c.getNombre()+" "+c.getApellidos()).orElse("-");
        String titulo = "-";
        Copias cp = daoCop.obtenerCopia(a.getIdSku());
        if (cp != null) {
            Peliculas p = daoPel.obtenerPelicula(cp.getIdPelicula());
            if (p != null) titulo = p.getTitulo();
        }
        modelo.addRow(new Object[]{
                a.getIdAlquilan(),
                a.getIdCliente(),
                cliente,
                a.getIdSku(),
                titulo,
                a.getFechaAlquiler(),
                a.getFechaLimiteDevolucion(),
                a.getFechaDevolucionReal()
        });
    }

    // ==== Eventos ====
    private void wireEvents() {
        btnRefrescar.addActionListener(e -> {
            cargarCombosAltas();
            cargarCombosFiltros();
        });

        btnAlquilar.addActionListener(e -> registrarAlquiler());

        btnDevolver.addActionListener(e -> registrarDevolucion());

        btnEliminar.addActionListener(e -> eliminarAlquiler());

        btnListarActivos.addActionListener(e -> cargarActivos());

        btnListarTodos.addActionListener(e -> {
            if (chkTodos.isSelected()) cargarTodos();
            else cargarActivos(); // si no quiere ver devueltos, muestra activos
        });

        btnFiltrarCliente.addActionListener(e -> {
            Clientes sel = (Clientes) cbFiltroCliente.getSelectedItem();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Elige un cliente."); return; }
            modelo.setRowCount(0);
            for (Alquila a : daoAlq.listarPorCliente(sel.getIdCliente())) addRow(a);
        });

        btnFiltrarPelicula.addActionListener(e -> {
            Peliculas sel = (Peliculas) cbFiltroPelicula.getSelectedItem();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Elige una película."); return; }
            modelo.setRowCount(0);
            for (Alquila a : daoAlq.listarPorPelicula(sel.getIdPelicula())) addRow(a);
        });
    }

    private void registrarAlquiler() {
        Clientes cli = (Clientes) cbClientes.getSelectedItem();
        Copias   cp  = (Copias)   cbCopias.getSelectedItem();
        if (cli == null || cp == null) {
            JOptionPane.showMessageDialog(this, "Selecciona cliente y copia disponible.");
            return;
        }
        java.util.Date dAlq = (java.util.Date) spFechaAlq.getValue();
        java.util.Date dLim = (java.util.Date) spFechaLim.getValue();
        if (dLim.before(dAlq)) {
            JOptionPane.showMessageDialog(this, "La fecha límite no puede ser anterior a la de alquiler.");
            return;
        }

        Alquila a = new Alquila();
        a.setIdCliente(cli.getIdCliente());
        a.setIdSku(cp.getIdSku());
        a.setFechaAlquiler(new Date(dAlq.getTime()));
        a.setFechaLimiteDevolucion(new Date(dLim.getTime()));
        a.setFechaDevolucionReal(null);

        boolean ok = daoAlq.agregarAlquiler(a);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Alquiler registrado.");
            cargarCombosAltas(); // la copia deja de estar disponible
            if (chkTodos.isSelected()) cargarTodos(); else cargarActivos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el alquiler.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucion() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un alquiler de la tabla."); return; }

        Integer idAlquila = (Integer) tabla.getValueAt(row, 0);
        java.util.Date hoy = new java.util.Date();

        boolean ok = daoAlq.registrarDevolucion(idAlquila, new Date(hoy.getTime()));
        if (ok) {
            JOptionPane.showMessageDialog(this, "Devolución registrada.");
            cargarCombosAltas(); // la copia vuelve a estar disponible
            if (chkTodos.isSelected()) cargarTodos(); else cargarActivos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarAlquiler() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un alquiler."); return; }
        Integer idAlquila = (Integer) tabla.getValueAt(row, 0);
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar alquiler ID="+idAlquila+"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = daoAlq.eliminarAlquiler(idAlquila);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Alquiler eliminado.");
            if (chkTodos.isSelected()) cargarTodos(); else cargarActivos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AlquileresFrame().setVisible(true));
    }
}

