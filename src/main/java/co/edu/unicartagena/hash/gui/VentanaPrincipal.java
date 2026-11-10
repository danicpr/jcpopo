package co.edu.unicartagena.hash.gui;

import co.edu.unicartagena.hash.model.BenchmarkHash;
import co.edu.unicartagena.hash.model.FuncionHash;
import co.edu.unicartagena.hash.model.Registro;
import co.edu.unicartagena.hash.model.TablaHashArbol;
import co.edu.unicartagena.hash.model.TablaHashLista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz Gráfica del Taller Tablas Hash (Figura 1 del enunciado).
 * Diseño clásico en blanco y negro, simple y directo.
 */
public class VentanaPrincipal extends JPanel {

    private TablaHashLista tablaListaMult = new TablaHashLista(FuncionHash.METODO_MULTIPLICACION);
    private TablaHashLista tablaListaDiv = new TablaHashLista(FuncionHash.METODO_DIVISION);
    private TablaHashArbol tablaArbolMult = new TablaHashArbol(FuncionHash.METODO_MULTIPLICACION);
    private TablaHashArbol tablaArbolDiv = new TablaHashArbol(FuncionHash.METODO_DIVISION);

    private List<Registro> registrosCargados = new ArrayList<>();
    private File archivoActual = null;
    private String ultimoPlateBuscado = null;

    private JRadioButton rbMultiplicacion;
    private JRadioButton rbDivision;
    private JRadioButton rbLista;
    private JRadioButton rbArbol;
    private JLabel lblColisionesValor;
    private DefaultListModel<String> modeloSlots;
    private JList<String> listaSlots;
    private PanelVisualizacionSlot panelGrafico;
    private DefaultTableModel modeloTabla;
    private JTextField txtRutaArchivo;

    public VentanaPrincipal() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        inicializarComponentes();
        cargarArchivoPorDefecto();
    }

    public JFrame crearFrame() {
        JFrame frame = new JFrame("Taller Tablas Hash - Estructura de Datos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(980, 680);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private void inicializarComponentes() {
        // ==================== PANEL SUPERIOR (File, Operations, Opciones, Collisions) ====================
        JPanel panelSuperior = new JPanel(new BorderLayout(12, 0));
        panelSuperior.setBackground(Color.WHITE);

        // Bloques File y Operations (izquierda, igual a Figura 1)
        JPanel panelMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelMenu.setBackground(Color.WHITE);

        JPanel cajaFile = new JPanel(new GridLayout(4, 1, 2, 2));
        cajaFile.setBackground(Color.WHITE);
        cajaFile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel lblFile = new JLabel("File", SwingConstants.CENTER);
        lblFile.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        JButton btnOpen = new JButton("Open");
        JButton btnSave = new JButton("Save");
        JButton btnClose = new JButton("Close");
        cajaFile.add(lblFile);
        cajaFile.add(btnOpen);
        cajaFile.add(btnSave);
        cajaFile.add(btnClose);

        JPanel cajaOperations = new JPanel(new GridLayout(4, 1, 2, 2));
        cajaOperations.setBackground(Color.WHITE);
        cajaOperations.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel lblOps = new JLabel("Operations", SwingConstants.CENTER);
        lblOps.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        JButton btnInsert = new JButton("Insert");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        cajaOperations.add(lblOps);
        cajaOperations.add(btnInsert);
        cajaOperations.add(btnDelete);
        cajaOperations.add(btnSearch);

        panelMenu.add(cajaFile);
        panelMenu.add(cajaOperations);

        // Panel central de selección (Función Hash y Estructura Lista / Árbol)
        JPanel panelOpciones = new JPanel(new GridLayout(2, 1, 4, 4));
        panelOpciones.setBackground(Color.WHITE);

        JPanel panelFuncion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelFuncion.setBackground(Color.WHITE);
        panelFuncion.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Función Hash (m = 256)"));
        rbMultiplicacion = new JRadioButton("1) Multiplicación", true);
        rbDivision = new JRadioButton("2) División");
        rbMultiplicacion.setBackground(Color.WHITE);
        rbDivision.setBackground(Color.WHITE);
        ButtonGroup grupoFuncion = new ButtonGroup();
        grupoFuncion.add(rbMultiplicacion);
        grupoFuncion.add(rbDivision);
        panelFuncion.add(rbMultiplicacion);
        panelFuncion.add(rbDivision);

        JPanel panelEstructura = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelEstructura.setBackground(Color.WHITE);
        panelEstructura.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Estructura en cada Slot"));
        rbLista = new JRadioButton("Lista Enlazada Simple", true);
        rbArbol = new JRadioButton("Árbol Binario");
        rbLista.setBackground(Color.WHITE);
        rbArbol.setBackground(Color.WHITE);
        ButtonGroup grupoEstructura = new ButtonGroup();
        grupoEstructura.add(rbLista);
        grupoEstructura.add(rbArbol);
        JButton btnComparar = new JButton("Mejora % (Árbol vs Lista)");
        panelEstructura.add(rbLista);
        panelEstructura.add(rbArbol);
        panelEstructura.add(btnComparar);

        panelOpciones.add(panelFuncion);
        panelOpciones.add(panelEstructura);

        // Bloque Collisions (derecha, igual a Figura 1)
        JPanel panelColisiones = new JPanel(new BorderLayout(0, 4));
        panelColisiones.setBackground(Color.WHITE);
        panelColisiones.setPreferredSize(new Dimension(150, 95));
        JLabel lblTituloCol = new JLabel("Collisions", SwingConstants.CENTER);
        lblTituloCol.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblColisionesValor = new JLabel("0", SwingConstants.CENTER);
        lblColisionesValor.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblColisionesValor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelColisiones.add(lblTituloCol, BorderLayout.NORTH);
        panelColisiones.add(lblColisionesValor, BorderLayout.CENTER);

        panelSuperior.add(panelMenu, BorderLayout.WEST);
        panelSuperior.add(panelOpciones, BorderLayout.CENTER);
        panelSuperior.add(panelColisiones, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // ==================== PANEL CENTRAL T (Slots con > 1 registro y visualización) ====================
        JPanel panelT = new JPanel(new BorderLayout(8, 6));
        panelT.setBackground(Color.WHITE);
        panelT.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                new EmptyBorder(8, 8, 8, 8)));

        JLabel lblT = new JLabel("T   (Slots con más de un registro)");
        lblT.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelT.add(lblT, BorderLayout.NORTH);

        modeloSlots = new DefaultListModel<>();
        listaSlots = new JList<>(modeloSlots);
        listaSlots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSlots.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollSlots = new JScrollPane(listaSlots);
        scrollSlots.setPreferredSize(new Dimension(130, 380));
        scrollSlots.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelT.add(scrollSlots, BorderLayout.WEST);

        // Derecha de T: arriba el diagrama de nodos, abajo la tabla con los datos del slot
        JPanel panelDerechoT = new JPanel(new BorderLayout(0, 8));
        panelDerechoT.setBackground(Color.WHITE);

        panelGrafico = new PanelVisualizacionSlot();
        JScrollPane scrollGrafico = new JScrollPane(panelGrafico);
        scrollGrafico.setPreferredSize(new Dimension(700, 220));
        scrollGrafico.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelDerechoT.add(scrollGrafico, BorderLayout.NORTH);

        String[] columnas = {"Plate (ID)", "First Name", "Last Name", "Gender", "Llave k", "Slot h(k)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaRegistros = new JTable(modeloTabla);
        tablaRegistros.setRowHeight(22);
        JPanel contenedorTabla = new JPanel(new BorderLayout());
        contenedorTabla.add(tablaRegistros.getTableHeader(), BorderLayout.NORTH);
        contenedorTabla.add(tablaRegistros, BorderLayout.CENTER);
        JScrollPane scrollTabla = new JScrollPane(contenedorTabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelDerechoT.add(scrollTabla, BorderLayout.CENTER);

        panelT.add(panelDerechoT, BorderLayout.CENTER);
        add(panelT, BorderLayout.CENTER);

        // ==================== BARRA INFERIOR (Ruta del archivo cargado) ====================
        txtRutaArchivo = new JTextField();
        txtRutaArchivo.setEditable(false);
        txtRutaArchivo.setBackground(Color.WHITE);
        txtRutaArchivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                new EmptyBorder(4, 6, 4, 6)));
        add(txtRutaArchivo, BorderLayout.SOUTH);

        // ==================== EVENTOS ====================
        btnOpen.addActionListener(e -> abrirArchivo());
        btnSave.addActionListener(e -> guardarArchivo());
        btnClose.addActionListener(e -> cerrarTabla());
        btnInsert.addActionListener(e -> insertarRegistro());
        btnDelete.addActionListener(e -> borrarRegistro());
        btnSearch.addActionListener(e -> buscarRegistro());
        btnComparar.addActionListener(e -> mostrarComparativa());

        rbMultiplicacion.addActionListener(e -> actualizarVista(null));
        rbDivision.addActionListener(e -> actualizarVista(null));
        rbLista.addActionListener(e -> actualizarVista(null));
        rbArbol.addActionListener(e -> actualizarVista(null));

        listaSlots.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSlotSeleccionado();
            }
        });
    }

    private void cargarArchivoPorDefecto() {
        File f = new File("MOCK_DATA.csv");
        if (!f.exists()) {
            f = new File("PLATES.csv");
        }
        if (f.exists()) {
            cargarDesdeArchivo(f);
        }
    }

    public void cargarDesdeArchivo(File archivo) {
        try {
            tablaListaMult.cargarArchivo(archivo);
            tablaListaDiv.cargarArchivo(archivo);
            tablaArbolMult.cargarArchivo(archivo);
            tablaArbolDiv.cargarArchivo(archivo);
            registrosCargados.clear();

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Registro r = Registro.desdeCsv(linea);
                    if (r != null) {
                        registrosCargados.add(r);
                    }
                }
            }
            archivoActual = archivo;
            txtRutaArchivo.setText(archivo.getAbsolutePath());
            ultimoPlateBuscado = "05-9165610";
            int slotInicial = getMetodoActual() == FuncionHash.METODO_MULTIPLICACION ? 149 : 82;
            actualizarVista(slotInicial);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo: " + ex.getMessage());
        }
    }

    private int getMetodoActual() {
        return rbMultiplicacion.isSelected()
                ? FuncionHash.METODO_MULTIPLICACION
                : FuncionHash.METODO_DIVISION;
    }

    public void configurarModo(int metodoHash, boolean usarArbol, Integer slotSeleccionar, String plateResaltar) {
        if (metodoHash == FuncionHash.METODO_MULTIPLICACION) {
            rbMultiplicacion.setSelected(true);
        } else {
            rbDivision.setSelected(true);
        }
        if (usarArbol) {
            rbArbol.setSelected(true);
        } else {
            rbLista.setSelected(true);
        }
        this.ultimoPlateBuscado = plateResaltar;
        actualizarVista(slotSeleccionar);
    }

    private void actualizarVista(Integer slotPreferido) {
        int metodo = getMetodoActual();
        boolean esArbol = rbArbol.isSelected();

        int colisiones;
        if (!esArbol) {
            TablaHashLista t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaListaMult : tablaListaDiv;
            colisiones = t.getColisiones();
        } else {
            TablaHashArbol t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaArbolMult : tablaArbolDiv;
            colisiones = t.getColisiones();
        }
        lblColisionesValor.setText(String.valueOf(colisiones));

        modeloSlots.clear();
        int indiceListaSeleccionar = -1;

        for (int i = 0; i < FuncionHash.M; i++) {
            int cantidad;
            if (!esArbol) {
                TablaHashLista t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaListaMult : tablaListaDiv;
                cantidad = t.getSlot(i).getTamaño();
            } else {
                TablaHashArbol t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaArbolMult : tablaArbolDiv;
                cantidad = t.getSlot(i).getNumeroNodos();
            }
            // Requisito c): visualizar cada espacio (slot) con más de un registro
            if (cantidad > 1) {
                int pos = modeloSlots.size();
                modeloSlots.addElement(i + "  (" + cantidad + ")");
                if (slotPreferido != null && slotPreferido == i) {
                    indiceListaSeleccionar = pos;
                }
            }
        }

        if (modeloSlots.isEmpty()) {
            modeloTabla.setRowCount(0);
            panelGrafico.mostrarSlotLista(-1, null, null);
            return;
        }

        if (indiceListaSeleccionar >= 0) {
            listaSlots.setSelectedIndex(indiceListaSeleccionar);
            listaSlots.ensureIndexIsVisible(indiceListaSeleccionar);
        } else {
            listaSlots.setSelectedIndex(0);
        }
        mostrarSlotSeleccionado();
    }

    private void mostrarSlotSeleccionado() {
        String item = listaSlots.getSelectedValue();
        if (item == null) {
            return;
        }
        int slot = Integer.parseInt(item.split("\\s+")[0].trim());
        int metodo = getMetodoActual();
        boolean esArbol = rbArbol.isSelected();

        List<Registro> registrosSlot;
        if (!esArbol) {
            TablaHashLista t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaListaMult : tablaListaDiv;
            registrosSlot = t.obtenerRegistrosSlot(slot);
            panelGrafico.mostrarSlotLista(slot, registrosSlot, ultimoPlateBuscado);
        } else {
            TablaHashArbol t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaArbolMult : tablaArbolDiv;
            registrosSlot = t.obtenerRegistrosSlot(slot);
            panelGrafico.mostrarSlotArbol(slot, t.getSlot(slot), ultimoPlateBuscado);
        }

        modeloTabla.setRowCount(0);
        for (Registro r : registrosSlot) {
            int h = FuncionHash.calcularHash(r.getLlaveK(), metodo);
            modeloTabla.addRow(new Object[]{
                r.getPlate(), r.getFirstName(), r.getLastName(),
                r.getGender(), r.getLlaveK(), h
            });
        }
    }

    // a) Cargar archivo PLATES.csv o MOCK_DATA.csv desde cualquier ubicación
    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser(archivoActual != null ? archivoActual.getParentFile() : new File("."));
        fc.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            cargarDesdeArchivo(fc.getSelectedFile());
        }
    }

    // g) Guardar estado actual de la Tabla Hash
    private void guardarArchivo() {
        JFileChooser fc = new JFileChooser(archivoActual != null ? archivoActual.getParentFile() : new File("."));
        fc.setSelectedFile(new File("ESTADO_TABLA_HASH.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File destino = fc.getSelectedFile();
                if (!rbArbol.isSelected()) {
                    TablaHashLista t = (getMetodoActual() == FuncionHash.METODO_MULTIPLICACION)
                            ? tablaListaMult : tablaListaDiv;
                    t.guardarArchivo(destino);
                } else {
                    TablaHashArbol t = (getMetodoActual() == FuncionHash.METODO_MULTIPLICACION)
                            ? tablaArbolMult : tablaArbolDiv;
                    t.guardarArchivo(destino);
                }
                JOptionPane.showMessageDialog(this, "Estado guardado en:\n" + destino.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void cerrarTabla() {
        tablaListaMult.limpiar();
        tablaListaDiv.limpiar();
        tablaArbolMult.limpiar();
        tablaArbolDiv.limpiar();
        registrosCargados.clear();
        txtRutaArchivo.setText("");
        ultimoPlateBuscado = null;
        actualizarVista(null);
    }

    // d) Insertar un nuevo registro
    private void insertarRegistro() {
        JTextField txtPlate = new JTextField();
        JTextField txtFirst = new JTextField();
        JTextField txtLast = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtGender = new JTextField("Female");

        Object[] campos = {
            "Plate (ID):", txtPlate,
            "First Name:", txtFirst,
            "Last Name:", txtLast,
            "Email:", txtEmail,
            "Gender:", txtGender
        };

        int op = JOptionPane.showConfirmDialog(this, campos, "Insertar Registro", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            String plate = txtPlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Plate (ID) es obligatorio.");
                return;
            }
            Registro nuevo = new Registro(plate, txtFirst.getText(), txtLast.getText(),
                    txtEmail.getText(), txtGender.getText());

            tablaListaMult.insertar(nuevo);
            tablaListaDiv.insertar(nuevo);
            tablaArbolMult.insertar(nuevo);
            tablaArbolDiv.insertar(nuevo);
            registrosCargados.remove(nuevo);
            registrosCargados.add(nuevo);

            ultimoPlateBuscado = nuevo.getPlate();
            int slot = FuncionHash.calcularHash(nuevo.getLlaveK(), getMetodoActual());
            actualizarVista(slot);

            JOptionPane.showMessageDialog(this,
                    "Registro insertado:\nPlate: " + nuevo.getPlate()
                    + "\nLlave k (radix-2): " + nuevo.getLlaveK()
                    + "\nSlot h(k): " + slot);
        }
    }

    // e) Borrar registro según el id
    private void borrarRegistro() {
        String plate = JOptionPane.showInputDialog(this, "Ingrese el Plate (ID) a borrar:");
        if (plate == null || plate.trim().isEmpty()) {
            return;
        }
        plate = plate.trim();
        long k = FuncionHash.calcularLlaveRadix2(plate);
        int slot = FuncionHash.calcularHash(k, getMetodoActual());

        boolean b1 = tablaListaMult.eliminar(plate);
        tablaListaDiv.eliminar(plate);
        tablaArbolMult.eliminar(plate);
        tablaArbolDiv.eliminar(plate);
        registrosCargados.remove(new Registro(plate));

        if (b1) {
            ultimoPlateBuscado = null;
            actualizarVista(slot);
            JOptionPane.showMessageDialog(this, "Registro " + plate + " eliminado del slot " + slot + ".");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el registro con ID: " + plate);
        }
    }

    // f) Buscar registro por id
    private void buscarRegistro() {
        String plate = JOptionPane.showInputDialog(this, "Ingrese el Plate (ID) a buscar:", "05-9165610");
        if (plate == null || plate.trim().isEmpty()) {
            return;
        }
        plate = plate.trim();
        int metodo = getMetodoActual();
        Registro encontrado;
        if (!rbArbol.isSelected()) {
            TablaHashLista t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaListaMult : tablaListaDiv;
            encontrado = t.buscar(plate);
        } else {
            TablaHashArbol t = (metodo == FuncionHash.METODO_MULTIPLICACION) ? tablaArbolMult : tablaArbolDiv;
            encontrado = t.buscar(plate);
        }

        if (encontrado != null) {
            int slot = FuncionHash.calcularHash(encontrado.getLlaveK(), metodo);
            ultimoPlateBuscado = encontrado.getPlate();
            actualizarVista(slot);
            JOptionPane.showMessageDialog(this,
                    "Registro encontrado:\n"
                    + "Plate: " + encontrado.getPlate() + "\n"
                    + "Nombre: " + encontrado.getFirstName() + " " + encontrado.getLastName() + "\n"
                    + "Email: " + encontrado.getEmail() + "\n"
                    + "Gender: " + encontrado.getGender() + "\n"
                    + "Llave k (radix-2): " + encontrado.getLlaveK() + "\n"
                    + "Slot h(k) [Método 1 - Mult.]: " + FuncionHash.hashMultiplicacion(encontrado.getLlaveK()) + "\n"
                    + "Slot h(k) [Método 2 - Div.]:  " + FuncionHash.hashDivision(encontrado.getLlaveK()));
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el registro con ID: " + plate);
        }
    }

    // Parte 2: Comparativa porcentual entre Lista Enlazada y Árbol Binario
    private void mostrarComparativa() {
        String resultado = BenchmarkHash.compararRendimiento(registrosCargados, getMetodoActual());
        JOptionPane.showMessageDialog(this, resultado, "Parte 2: Lista vs. Árbol Binario", JOptionPane.INFORMATION_MESSAGE);
    }
}
