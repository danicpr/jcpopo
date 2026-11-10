package co.edu.unicartagena.hash.gui;

import co.edu.unicartagena.hash.model.BenchmarkHash;
import co.edu.unicartagena.hash.model.FuncionHash;
import co.edu.unicartagena.hash.model.Registro;
import co.edu.unicartagena.hash.model.TablaHashArbol;
import co.edu.unicartagena.hash.model.TablaHashLista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz Gráfica Principal del Taller de Tablas Hash y Árboles Binarios.
 * Implementa el diseño de la Figura 1 del enunciado y todas las operaciones (a - g)
 * tanto para Lista Enlazada Simple como para Árbol Binario de Búsqueda.
 */
public class VentanaPrincipal extends JPanel {
    private JFrame frameContenedor;
    private JMenuBar menuBarPrincipal;

    private final TablaHashLista tablaLista;
    private final TablaHashArbol tablaArbol;
    private boolean usarArbolBinario = false;
    private File archivoActual;
    private String placaResaltada = null;

    // Componentes de la interfaz (Figura 1)
    private final JLabel lblCollisionsValor;
    private final JLabel lblCollisionsDetalle;
    private final JRadioButton rbMultiplicacion;
    private final JRadioButton rbDivision;
    private final JRadioButton rbEstructuraLista;
    private final JRadioButton rbEstructuraArbol;
    private final JCheckBox chkSoloMasDeUnRegistro;
    private final DefaultListModel<String> modeloListaSlots;
    private final JList<String> listaSlotsT;
    private final PanelVisualizacionSlot panelGraficoSlot;
    private final DefaultTableModel modeloTablaRegistros;
    private final JTable tablaRegistrosSlot;
    private final JTextField txtFormulaRadix2;
    private final JTextField txtRutaArchivo;
    private final JLabel lblResumenEstado;

    public VentanaPrincipal() {
        super(new BorderLayout(10, 10));
        this.tablaLista = new TablaHashLista(FuncionHash.Metodo.MULTIPLICACION);
        this.tablaArbol = new TablaHashArbol(FuncionHash.Metodo.MULTIPLICACION);

        setSize(1120, 760);
        setPreferredSize(new Dimension(1120, 760));
        this.menuBarPrincipal = crearBarraMenu();

        setBorder(new EmptyBorder(10, 12, 10, 12));
        setBackground(new Color(248, 250, 252));

        // ================= PANEL SUPERIOR (File, Operations, Config, Collisions) =================
        JPanel panelSuperior = new JPanel(new BorderLayout(12, 0));
        panelSuperior.setOpaque(false);

        // Izquierda: Bloques File y Operations tal como en la Figura 1
        JPanel panelMenusFigura1 = new JPanel(new GridLayout(1, 2, 10, 0));
        panelMenusFigura1.setOpaque(false);

        JPanel panelFile = new JPanel(new GridLayout(4, 1, 2, 3));
        panelFile.setBackground(Color.WHITE);
        panelFile.setBorder(new LineBorder(new Color(100, 116, 139), 1));
        JLabel lblFileHeader = new JLabel("File", SwingConstants.CENTER);
        lblFileHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblFileHeader.setOpaque(true);
        lblFileHeader.setBackground(new Color(226, 232, 240));
        lblFileHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 116, 139)));

        JButton btnOpen = crearBotonMenu("Open");
        JButton btnSave = crearBotonMenu("Save");
        JButton btnClose = crearBotonMenu("Close");
        btnOpen.addActionListener(e -> accionAbrirArchivo());
        btnSave.addActionListener(e -> accionGuardarEstado());
        btnClose.addActionListener(e -> accionCerrarTabla());

        panelFile.add(lblFileHeader);
        panelFile.add(btnOpen);
        panelFile.add(btnSave);
        panelFile.add(btnClose);

        JPanel panelOperations = new JPanel(new GridLayout(4, 1, 2, 3));
        panelOperations.setBackground(Color.WHITE);
        panelOperations.setBorder(new LineBorder(new Color(100, 116, 139), 1));
        JLabel lblOpsHeader = new JLabel("Operations", SwingConstants.CENTER);
        lblOpsHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblOpsHeader.setOpaque(true);
        lblOpsHeader.setBackground(new Color(226, 232, 240));
        lblOpsHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 116, 139)));

        JButton btnInsert = crearBotonMenu("Insert");
        JButton btnDelete = crearBotonMenu("Delete");
        JButton btnSearch = crearBotonMenu("Search");
        btnInsert.addActionListener(e -> accionInsertarRegistro());
        btnDelete.addActionListener(e -> accionBorrarRegistro());
        btnSearch.addActionListener(e -> accionBuscarRegistro());

        panelOperations.add(lblOpsHeader);
        panelOperations.add(btnInsert);
        panelOperations.add(btnDelete);
        panelOperations.add(btnSearch);

        panelMenusFigura1.add(panelFile);
        panelMenusFigura1.add(panelOperations);
        panelMenusFigura1.setPreferredSize(new Dimension(230, 118));

        // Centro: Selectores de Función Hash, Estructura por Slot y Botón de Benchmark
        JPanel panelConfiguracion = new JPanel(new GridLayout(2, 1, 6, 6));
        panelConfiguracion.setOpaque(false);

        JPanel panelFuncionHash = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelFuncionHash.setBackground(Color.WHITE);
        panelFuncionHash.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(203, 213, 225)),
                "Función Hash (m = 256, Llave k en Radix-2 ASCII)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(30, 41, 59)));

        rbMultiplicacion = new JRadioButton("1) Multiplicación: h(k) = ⌊m(kA mod 1)⌋", true);
        rbDivision = new JRadioButton("2) División: h(k) = k mod m", false);
        rbMultiplicacion.setOpaque(false);
        rbDivision.setOpaque(false);
        rbMultiplicacion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rbDivision.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ButtonGroup grupoHash = new ButtonGroup();
        grupoHash.add(rbMultiplicacion);
        grupoHash.add(rbDivision);

        rbMultiplicacion.addActionListener(e -> cambiarMetodoHash(FuncionHash.Metodo.MULTIPLICACION));
        rbDivision.addActionListener(e -> cambiarMetodoHash(FuncionHash.Metodo.DIVISION));

        panelFuncionHash.add(rbMultiplicacion);
        panelFuncionHash.add(rbDivision);

        JPanel panelEstructura = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelEstructura.setBackground(Color.WHITE);
        panelEstructura.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(203, 213, 225)),
                "Estructura de Almacenamiento en cada Slot (T[i])",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(30, 41, 59)));

        rbEstructuraLista = new JRadioButton("Lista Enlazada Simple (Sesión 8)", true);
        rbEstructuraArbol = new JRadioButton("Árbol Binario (BinaryTree)", false);
        rbEstructuraLista.setOpaque(false);
        rbEstructuraArbol.setOpaque(false);
        rbEstructuraLista.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rbEstructuraArbol.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ButtonGroup grupoEst = new ButtonGroup();
        grupoEst.add(rbEstructuraLista);
        grupoEst.add(rbEstructuraArbol);

        rbEstructuraLista.addActionListener(e -> cambiarEstructuraSlot(false));
        rbEstructuraArbol.addActionListener(e -> cambiarEstructuraSlot(true));

        JButton btnBenchmark = new JButton("Comparativa % (Lista vs. Árbol)");
        btnBenchmark.setBackground(new Color(37, 99, 235));
        btnBenchmark.setForeground(Color.WHITE);
        btnBenchmark.setFocusPainted(false);
        btnBenchmark.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnBenchmark.addActionListener(e -> accionMostrarBenchmark());

        panelEstructura.add(rbEstructuraLista);
        panelEstructura.add(rbEstructuraArbol);
        panelEstructura.add(btnBenchmark);

        panelConfiguracion.add(panelFuncionHash);
        panelConfiguracion.add(panelEstructura);

        // Derecha: Recuadro "Collisions" tal como en la Figura 1
        JPanel panelCollisions = new JPanel(new BorderLayout(2, 2));
        panelCollisions.setOpaque(false);
        panelCollisions.setPreferredSize(new Dimension(225, 118));

        JLabel lblCollisionsTitulo = new JLabel("Collisions", SwingConstants.CENTER);
        lblCollisionsTitulo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblCollisionsTitulo.setForeground(new Color(15, 23, 42));

        JPanel cajaCollisionsNumero = new JPanel(new BorderLayout());
        cajaCollisionsNumero.setBackground(Color.WHITE);
        cajaCollisionsNumero.setBorder(new LineBorder(new Color(30, 41, 59), 2));

        lblCollisionsValor = new JLabel("0", SwingConstants.CENTER);
        lblCollisionsValor.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblCollisionsValor.setForeground(new Color(15, 23, 42));

        lblCollisionsDetalle = new JLabel("Slots >1: 0 | Compartidos: 0", SwingConstants.CENTER);
        lblCollisionsDetalle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblCollisionsDetalle.setForeground(new Color(71, 85, 105));
        lblCollisionsDetalle.setBorder(new EmptyBorder(0, 4, 4, 4));

        cajaCollisionsNumero.add(lblCollisionsValor, BorderLayout.CENTER);
        cajaCollisionsNumero.add(lblCollisionsDetalle, BorderLayout.SOUTH);

        panelCollisions.add(lblCollisionsTitulo, BorderLayout.NORTH);
        panelCollisions.add(cajaCollisionsNumero, BorderLayout.CENTER);

        panelSuperior.add(panelMenusFigura1, BorderLayout.WEST);
        panelSuperior.add(panelConfiguracion, BorderLayout.CENTER);
        panelSuperior.add(panelCollisions, BorderLayout.EAST);

        // ================= PANEL CENTRAL "T" (Figura 1) =================
        JPanel panelCentralT = new JPanel(new BorderLayout(8, 8));
        panelCentralT.setBackground(Color.WHITE);
        panelCentralT.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        // Cabecera de T
        JPanel panelCabeceraT = new JPanel(new BorderLayout(10, 0));
        panelCabeceraT.setOpaque(false);
        JLabel lblTituloT = new JLabel("  T  (Espacios / Slots de la Tabla Hash)");
        lblTituloT.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloT.setForeground(new Color(15, 23, 42));

        chkSoloMasDeUnRegistro = new JCheckBox("Solo slots con > 1 reg.", true);
        chkSoloMasDeUnRegistro.setOpaque(false);
        chkSoloMasDeUnRegistro.setFont(new Font("SansSerif", Font.BOLD, 11));
        chkSoloMasDeUnRegistro.addActionListener(e -> refrescarListaSlots(-1));

        JPanel panelIzqCabeceraT = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelIzqCabeceraT.setOpaque(false);
        panelIzqCabeceraT.add(lblTituloT);
        panelIzqCabeceraT.add(chkSoloMasDeUnRegistro);

        lblResumenEstado = new JLabel("Registros: 0 | m = 256");
        lblResumenEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblResumenEstado.setForeground(new Color(37, 99, 235));

        panelCabeceraT.add(panelIzqCabeceraT, BorderLayout.WEST);
        panelCabeceraT.add(lblResumenEstado, BorderLayout.EAST);

        // Columna izquierda de T: lista vertical de slots
        modeloListaSlots = new DefaultListModel<>();
        listaSlotsT = new JList<>(modeloListaSlots);
        listaSlotsT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSlotsT.setFont(new Font("Monospaced", Font.BOLD, 13));
        listaSlotsT.setFixedCellHeight(28);
        listaSlotsT.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSlotSeleccionado();
            }
        });

        JScrollPane scrollSlots = new JScrollPane(listaSlotsT);
        scrollSlots.setPreferredSize(new Dimension(175, 420));
        scrollSlots.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(148, 163, 184)),
                "Slot [Índice] (Cant.)",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11)));

        // Panel derecho de T: visualización gráfica arriba + tabla de registros abajo
        panelGraficoSlot = new PanelVisualizacionSlot();
        JScrollPane scrollGrafico = new JScrollPane(panelGraficoSlot);
        scrollGrafico.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(148, 163, 184)),
                "Visualización de Nodos en el Slot Seleccionado",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11)));

        String[] columnas = {"#", "Plate (ID)", "First Name", "Last Name", "Email", "Gender", "Llave k (Radix-2)", "h(k) Mult.", "h(k) Div."};
        modeloTablaRegistros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRegistrosSlot = new JTable(modeloTablaRegistros);
        tablaRegistrosSlot.setRowHeight(23);
        tablaRegistrosSlot.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tablaRegistrosSlot.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tablaRegistrosSlot.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRegistrosSlot.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarInspectorRadix2DesdeTabla();
            }
        });

        JPanel panelTablaConHeader = new JPanel(new BorderLayout());
        panelTablaConHeader.add(tablaRegistrosSlot.getTableHeader(), BorderLayout.NORTH);
        panelTablaConHeader.add(tablaRegistrosSlot, BorderLayout.CENTER);
        JScrollPane scrollTabla = new JScrollPane(panelTablaConHeader);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(148, 163, 184)),
                "Registros Almacenados en el Slot Seleccionado",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11)));

        JPanel panelInspectorRadix = new JPanel(new BorderLayout(6, 0));
        panelInspectorRadix.setOpaque(false);
        panelInspectorRadix.setBorder(new EmptyBorder(4, 2, 0, 2));
        JLabel lblRadix = new JLabel("Reducción ASCII (Radix-2): ");
        lblRadix.setFont(new Font("SansSerif", Font.BOLD, 11));
        txtFormulaRadix2 = new JTextField();
        txtFormulaRadix2.setEditable(false);
        txtFormulaRadix2.setFont(new Font("Monospaced", Font.BOLD, 10));
        txtFormulaRadix2.setBackground(new Color(241, 245, 249));
        panelInspectorRadix.add(lblRadix, BorderLayout.WEST);
        panelInspectorRadix.add(txtFormulaRadix2, BorderLayout.CENTER);

        JPanel panelInferiorDerecho = new JPanel(new BorderLayout(0, 4));
        panelInferiorDerecho.setOpaque(false);
        panelInferiorDerecho.add(scrollTabla, BorderLayout.CENTER);
        panelInferiorDerecho.add(panelInspectorRadix, BorderLayout.SOUTH);

        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollGrafico, panelInferiorDerecho);
        splitDerecho.setResizeWeight(0.54);
        splitDerecho.setDividerLocation(245);
        splitDerecho.setBorder(null);

        panelCentralT.add(panelCabeceraT, BorderLayout.NORTH);
        panelCentralT.add(scrollSlots, BorderLayout.WEST);
        panelCentralT.add(splitDerecho, BorderLayout.CENTER);

        // ================= BARRA INFERIOR: RUTA DEL ARCHIVO (Figura 1) =================
        JPanel panelInferiorRuta = new JPanel(new BorderLayout(6, 0));
        panelInferiorRuta.setOpaque(false);
        txtRutaArchivo = new JTextField("Ningún archivo cargado");
        txtRutaArchivo.setEditable(false);
        txtRutaArchivo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtRutaArchivo.setBackground(Color.WHITE);
        txtRutaArchivo.setBorder(new CompoundBorder(
                new LineBorder(new Color(30, 41, 59), 1),
                new EmptyBorder(4, 8, 4, 8)));
        panelInferiorRuta.add(txtRutaArchivo, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentralT, BorderLayout.CENTER);
        add(panelInferiorRuta, BorderLayout.SOUTH);

        // Cargar automáticamente MOCK_DATA.csv o PLATES.csv si existe en el directorio de trabajo
        intentarCargaInicial();
    }

    public JFrame crearFrame() {
        if (frameContenedor == null) {
            frameContenedor = new JFrame("Taller Tablas Hash & Árbol Binario — Estructura de Datos (Universidad de Cartagena)");
            frameContenedor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameContenedor.setSize(1120, 780);
            frameContenedor.setMinimumSize(new Dimension(980, 680));
            frameContenedor.setLocationRelativeTo(null);
            if (menuBarPrincipal != null) {
                frameContenedor.setJMenuBar(menuBarPrincipal);
            }
            frameContenedor.setContentPane(this);
        }
        return frameContenedor;
    }

    public JMenuBar getMenuBarPrincipal() {
        return menuBarPrincipal;
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem itemOpen = new JMenuItem("Open (Cargar PLATES.csv / MOCK_DATA.csv)");
        JMenuItem itemSave = new JMenuItem("Save (Guardar estado actual de la Tabla Hash)");
        JMenuItem itemClose = new JMenuItem("Close (Limpiar / Cerrar archivo)");
        itemOpen.addActionListener(e -> accionAbrirArchivo());
        itemSave.addActionListener(e -> accionGuardarEstado());
        itemClose.addActionListener(e -> accionCerrarTabla());
        menuFile.add(itemOpen);
        menuFile.add(itemSave);
        menuFile.addSeparator();
        menuFile.add(itemClose);

        JMenu menuOperations = new JMenu("Operations");
        JMenuItem itemInsert = new JMenuItem("Insert (Insertar nuevo registro)");
        JMenuItem itemDelete = new JMenuItem("Delete (Borrar registro según el id)");
        JMenuItem itemSearch = new JMenuItem("Search (Buscar registro por id)");
        JMenuItem itemBenchmark = new JMenuItem("Benchmark (Comparar Lista vs. Árbol Binario)");
        itemInsert.addActionListener(e -> accionInsertarRegistro());
        itemDelete.addActionListener(e -> accionBorrarRegistro());
        itemSearch.addActionListener(e -> accionBuscarRegistro());
        itemBenchmark.addActionListener(e -> accionMostrarBenchmark());
        menuOperations.add(itemInsert);
        menuOperations.add(itemDelete);
        menuOperations.add(itemSearch);
        menuOperations.addSeparator();
        menuOperations.add(itemBenchmark);

        menuBar.add(menuFile);
        menuBar.add(menuOperations);
        return menuBar;
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(2, 10, 2, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void intentarCargaInicial() {
        File fMock = new File("MOCK_DATA.csv");
        File fPlates = new File("PLATES.csv");
        if (fMock.exists() && fMock.isFile()) {
            cargarArchivo(fMock, false);
        } else if (fPlates.exists() && fPlates.isFile()) {
            cargarArchivo(fPlates, false);
        } else {
            refrescarEstadisticasYVista(-1);
        }
    }

    public void cargarArchivo(File archivo, boolean mostrarDialogo) {
        try {
            int nLista = tablaLista.cargarDesdeCSV(archivo);
            tablaArbol.cargarDesdeCSV(archivo);
            this.archivoActual = archivo;
            this.placaResaltada = "05-9165610";
            txtRutaArchivo.setText(archivo.getAbsolutePath());

            int slotEjemplo = getMetodoActivo() == FuncionHash.Metodo.MULTIPLICACION ? 149 : 82;
            refrescarEstadisticasYVista(slotEjemplo);

            if (mostrarDialogo) {
                JOptionPane.showMessageDialog(this,
                        "Se cargaron exitosamente " + nLista + " registros desde:\n" + archivo.getAbsolutePath(),
                        "Archivo CSV Cargado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer el archivo CSV:\n" + ex.getMessage(),
                    "Error de Lectura", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FuncionHash.Metodo getMetodoActivo() {
        return rbMultiplicacion.isSelected() ? FuncionHash.Metodo.MULTIPLICACION : FuncionHash.Metodo.DIVISION;
    }

    public void cambiarMetodoHash(FuncionHash.Metodo metodo) {
        if (metodo == FuncionHash.Metodo.MULTIPLICACION) {
            rbMultiplicacion.setSelected(true);
        } else {
            rbDivision.setSelected(true);
        }
        tablaLista.setMetodo(metodo);
        tablaArbol.setMetodo(metodo);
        int slotPreferido = -1;
        if (placaResaltada != null) {
            slotPreferido = FuncionHash.calcularHash(FuncionHash.calcularLlaveRadix2(placaResaltada), metodo);
        }
        refrescarEstadisticasYVista(slotPreferido);
    }

    public void cambiarEstructuraSlot(boolean arbol) {
        this.usarArbolBinario = arbol;
        if (arbol) {
            rbEstructuraArbol.setSelected(true);
        } else {
            rbEstructuraLista.setSelected(true);
        }
        int slotActual = obtenerIndiceSlotSeleccionado();
        refrescarEstadisticasYVista(slotActual);
    }

    public void seleccionarSlotYPlaca(int slot, String placa) {
        this.placaResaltada = placa;
        refrescarEstadisticasYVista(slot);
    }

    private void refrescarEstadisticasYVista(int slotPreferido) {
        int colisionesExceso = usarArbolBinario ? tablaArbol.getNumeroColisiones() : tablaLista.getNumeroColisiones();
        int objetosCompartidos = usarArbolBinario
                ? tablaArbol.getTotalObjetosEnSlotsConColision()
                : tablaLista.getTotalObjetosEnSlotsConColision();
        int slotsConColision = usarArbolBinario
                ? tablaArbol.getCantidadSlotsConColision()
                : tablaLista.getCantidadSlotsConColision();
        int slotsOcupados = usarArbolBinario
                ? tablaArbol.getCantidadSlotsOcupados()
                : tablaLista.getCantidadSlotsOcupados();
        int totalReg = usarArbolBinario ? tablaArbol.getTotalRegistros() : tablaLista.getTotalRegistros();

        lblCollisionsValor.setText(String.valueOf(colisionesExceso));
        lblCollisionsDetalle.setText("Slots >1: " + slotsConColision + " | En colisión: " + objetosCompartidos);
        lblCollisionsValor.setToolTipText(
                "Colisiones por inserción (∑(|slot|-1)): " + colisionesExceso
                + " | Total objetos que comparten slot (∑|slot| con |slot|>1): " + objetosCompartidos
                + " | Slots con >1 registro: " + slotsConColision);

        String nombreEst = usarArbolBinario ? "Árbol Binario (ABB)" : "Lista Enlazada Simple";
        lblResumenEstado.setText(String.format(
                "Estructura: %s  |  Registros: %d  |  Slots ocupados: %d/256  |  Slots con >1 reg.: %d",
                nombreEst, totalReg, slotsOcupados, slotsConColision));

        refrescarListaSlots(slotPreferido);
    }

    private void refrescarListaSlots(int slotPreferido) {
        modeloListaSlots.clear();
        boolean soloMasDeUno = chkSoloMasDeUnRegistro.isSelected();
        int indiceSeleccionar = -1;
        int idxActual = 0;

        for (int i = 0; i < FuncionHash.M; i++) {
            int cant = usarArbolBinario ? tablaArbol.getSlot(i).getNumeroNodos() : tablaLista.getSlot(i).getTamaño();
            if (!soloMasDeUno || cant > 1) {
                modeloListaSlots.addElement(String.format("%3d   (%d reg.)", i, cant));
                if (i == slotPreferido) {
                    indiceSeleccionar = idxActual;
                }
                idxActual++;
            }
        }

        if (modeloListaSlots.getSize() > 0) {
            if (indiceSeleccionar >= 0) {
                listaSlotsT.setSelectedIndex(indiceSeleccionar);
                listaSlotsT.ensureIndexIsVisible(indiceSeleccionar);
            } else {
                listaSlotsT.setSelectedIndex(0);
            }
        } else {
            panelGraficoSlot.actualizarVistaLista(-1, null, null);
            modeloTablaRegistros.setRowCount(0);
            txtFormulaRadix2.setText("");
        }
    }

    private int obtenerIndiceSlotSeleccionado() {
        String sel = listaSlotsT.getSelectedValue();
        if (sel == null || sel.trim().isEmpty()) {
            return -1;
        }
        String[] partes = sel.trim().split("\\s+");
        try {
            return Integer.parseInt(partes[0]);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void mostrarSlotSeleccionado() {
        int slot = obtenerIndiceSlotSeleccionado();
        if (slot < 0) {
            return;
        }
        List<Registro> registros = usarArbolBinario
                ? tablaArbol.getRegistrosEnSlot(slot)
                : tablaLista.getRegistrosEnSlot(slot);

        if (usarArbolBinario) {
            panelGraficoSlot.actualizarVistaArbol(slot, tablaArbol.getSlot(slot), registros, placaResaltada);
        } else {
            panelGraficoSlot.actualizarVistaLista(slot, registros, placaResaltada);
        }

        modeloTablaRegistros.setRowCount(0);
        int filaResaltada = -1;
        for (int i = 0; i < registros.size(); i++) {
            Registro r = registros.get(i);
            long k = r.getK();
            modeloTablaRegistros.addRow(new Object[]{
                i + 1,
                r.getPlate(),
                r.getFirstName(),
                r.getLastName(),
                r.getEmail(),
                r.getGender(),
                k,
                FuncionHash.hashMultiplicacion(k),
                FuncionHash.hashDivision(k)
            });
            if (placaResaltada != null && placaResaltada.equalsIgnoreCase(r.getPlate())) {
                filaResaltada = i;
            }
        }

        if (filaResaltada >= 0) {
            tablaRegistrosSlot.setRowSelectionInterval(filaResaltada, filaResaltada);
        } else if (modeloTablaRegistros.getRowCount() > 0) {
            tablaRegistrosSlot.setRowSelectionInterval(0, 0);
        } else {
            txtFormulaRadix2.setText("");
        }
    }

    private void actualizarInspectorRadix2DesdeTabla() {
        int fila = tablaRegistrosSlot.getSelectedRow();
        if (fila < 0) {
            return;
        }
        String plate = String.valueOf(modeloTablaRegistros.getValueAt(fila, 1));
        long k = FuncionHash.calcularLlaveRadix2(plate);
        int h1 = FuncionHash.hashMultiplicacion(k);
        int h2 = FuncionHash.hashDivision(k);
        String expr = FuncionHash.explicarReduccionAscii(plate);
        txtFormulaRadix2.setText(plate + " => " + expr + " = " + k + " [h1=" + h1 + ", h2=" + h2 + "]");
    }

    // ================= ACCIONES DE ARCHIVO (a, g) =================

    private void accionAbrirArchivo() {
        JFileChooser chooser = new JFileChooser(archivoActual != null ? archivoActual.getParentFile() : new File("."));
        chooser.setDialogTitle("Seleccionar archivo CSV (PLATES.csv / MOCK_DATA.csv)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv", "txt"));
        int resp = chooser.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            cargarArchivo(chooser.getSelectedFile(), true);
        }
    }

    private void accionGuardarEstado() {
        JFileChooser chooser = new JFileChooser(archivoActual != null ? archivoActual.getParentFile() : new File("."));
        chooser.setDialogTitle("Guardar estado actual de la Tabla Hash");
        chooser.setSelectedFile(new File("Estado_Tabla_Hash.csv"));
        int resp = chooser.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            File destino = chooser.getSelectedFile();
            try {
                if (usarArbolBinario) {
                    tablaArbol.guardarEstado(destino);
                } else {
                    tablaLista.guardarEstado(destino);
                }
                JOptionPane.showMessageDialog(this,
                        "Estado actual de la Tabla Hash guardado exitosamente en:\n" + destino.getAbsolutePath(),
                        "Estado Guardado", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el estado:\n" + ex.getMessage(),
                        "Error de Escritura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionCerrarTabla() {
        tablaLista.limpiar();
        tablaArbol.limpiar();
        archivoActual = null;
        placaResaltada = null;
        txtRutaArchivo.setText("Tabla Hash reiniciada (sin archivo activo)");
        refrescarEstadisticasYVista(-1);
    }

    // ================= OPERACIONES DE LA TABLA HASH (d, e, f) =================

    private void accionInsertarRegistro() {
        JTextField txtPlate = new JTextField(15);
        JTextField txtFirstName = new JTextField(15);
        JTextField txtLastName = new JTextField(15);
        JTextField txtEmail = new JTextField(18);
        JComboBox<String> cmbGender = new JComboBox<>(new String[]{"Female", "Male", "Non-binary", "Other"});

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Plate (ID):", "First Name:", "Last Name:", "Email:", "Gender:"};
        java.awt.Component[] fields = {txtPlate, txtFirstName, txtLastName, txtEmail, cmbGender};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(fields[i], gbc);
        }

        int op = JOptionPane.showConfirmDialog(this, form,
                "Insertar Nuevo Registro en la Tabla Hash", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op == JOptionPane.OK_OPTION) {
            String plate = txtPlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Plate (ID) es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Registro nuevo = new Registro(
                    plate,
                    txtFirstName.getText().trim(),
                    txtLastName.getText().trim(),
                    txtEmail.getText().trim(),
                    String.valueOf(cmbGender.getSelectedItem())
            );

            long t0 = System.nanoTime();
            tablaLista.insertar(nuevo);
            long tLista = System.nanoTime() - t0;

            long t1 = System.nanoTime();
            tablaArbol.insertar(nuevo);
            long tArbol = System.nanoTime() - t1;

            int slot = nuevo.getHash(getMetodoActivo());
            placaResaltada = nuevo.getPlate();
            int cantEnSlot = usarArbolBinario ? tablaArbol.getSlot(slot).getNumeroNodos() : tablaLista.getSlot(slot).getTamaño();
            if (cantEnSlot <= 1 && chkSoloMasDeUnRegistro.isSelected()) {
                chkSoloMasDeUnRegistro.setSelected(false);
            }
            refrescarEstadisticasYVista(slot);

            JOptionPane.showMessageDialog(this,
                    String.format("Registro insertado correctamente:\n\n"
                            + "Plate (ID) : %s\n"
                            + "Nombre     : %s %s\n"
                            + "Llave k    : %d\n"
                            + "Slot h(k)  : %d (%s)\n"
                            + "Tiempo Lista: %d ns  |  Tiempo Árbol: %d ns",
                            nuevo.getPlate(), nuevo.getFirstName(), nuevo.getLastName(),
                            nuevo.getK(), slot, getMetodoActivo().getEtiqueta(), tLista, tArbol),
                    "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionBorrarRegistro() {
        String plate = JOptionPane.showInputDialog(this,
                "Ingrese el ID (Plate) del registro que desea borrar (ej. 05-9165610):",
                "Borrar Registro por ID", JOptionPane.QUESTION_MESSAGE);
        if (plate == null || plate.trim().isEmpty()) {
            return;
        }
        String limpio = plate.trim();
        long k = FuncionHash.calcularLlaveRadix2(limpio);
        int slot = FuncionHash.calcularHash(k, getMetodoActivo());

        long t0 = System.nanoTime();
        boolean borradoLista = tablaLista.eliminar(limpio);
        long tLista = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        boolean borradoArbol = tablaArbol.eliminar(limpio);
        long tArbol = System.nanoTime() - t1;

        boolean exito = usarArbolBinario ? borradoArbol : borradoLista;
        if (exito) {
            placaResaltada = null;
            refrescarEstadisticasYVista(slot);
            JOptionPane.showMessageDialog(this,
                    String.format("Registro con ID '%s' (k=%d) eliminado del Slot T[%d].\n"
                            + "Tiempo en Lista: %d ns  |  Tiempo en Árbol Binario: %d ns",
                            limpio, k, slot, tLista, tArbol),
                    "Registro Eliminado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.format("No se encontró ningún registro con ID '%s' (k=%d, Slot esperado T[%d]).",
                            limpio, k, slot),
                    "Registro No Encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionBuscarRegistro() {
        String plate = JOptionPane.showInputDialog(this,
                "Ingrese el ID (Plate) del registro a buscar (ej. 05-9165610):",
                "Buscar Registro por ID", JOptionPane.QUESTION_MESSAGE);
        if (plate == null || plate.trim().isEmpty()) {
            return;
        }
        String limpio = plate.trim();
        long k = FuncionHash.calcularLlaveRadix2(limpio);
        int slot = FuncionHash.calcularHash(k, getMetodoActivo());

        long t0 = System.nanoTime();
        Registro regLista = tablaLista.buscar(limpio);
        long tLista = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        Registro regArbol = tablaArbol.buscar(limpio);
        long tArbol = System.nanoTime() - t1;

        Registro encontrado = usarArbolBinario ? regArbol : regLista;
        if (encontrado != null) {
            placaResaltada = encontrado.getPlate();
            int cantEnSlot = usarArbolBinario ? tablaArbol.getSlot(slot).getNumeroNodos() : tablaLista.getSlot(slot).getTamaño();
            if (cantEnSlot <= 1 && chkSoloMasDeUnRegistro.isSelected()) {
                chkSoloMasDeUnRegistro.setSelected(false);
            }
            refrescarEstadisticasYVista(slot);

            JOptionPane.showMessageDialog(this,
                    String.format("Registro localizado en T[%d]:\n\n"
                            + "Plate (ID)      : %s\n"
                            + "First Name      : %s\n"
                            + "Last Name       : %s\n"
                            + "Email           : %s\n"
                            + "Gender          : %s\n"
                            + "Reducción ASCII : %s\n"
                            + "Llave k         : %d\n"
                            + "h(k) Método 1   : %d\n"
                            + "h(k) Método 2   : %d\n"
                            + "Tiempo Búsqueda : %d ns (Lista) vs. %d ns (Árbol Binario)",
                            slot, encontrado.getPlate(), encontrado.getFirstName(), encontrado.getLastName(),
                            encontrado.getEmail(), encontrado.getGender(),
                            FuncionHash.explicarReduccionAscii(encontrado.getPlate()),
                            encontrado.getK(),
                            FuncionHash.hashMultiplicacion(encontrado.getK()),
                            FuncionHash.hashDivision(encontrado.getK()),
                            tLista, tArbol),
                    "Resultado de Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.format("El registro con ID '%s' (k=%d, Slot T[%d]) no existe en la Tabla Hash.",
                            limpio, k, slot),
                    "Sin Coincidencias", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionMostrarBenchmark() {
        List<Registro> actuales = tablaLista.obtenerTodosLosRegistros();
        BenchmarkHash.ResultadoMetodo resMult = BenchmarkHash.ejecutarBenchmark(actuales, FuncionHash.Metodo.MULTIPLICACION, 200);
        BenchmarkHash.ResultadoMetodo resDiv = BenchmarkHash.ejecutarBenchmark(actuales, FuncionHash.Metodo.DIVISION, 200);

        double promMejoraTiempoIns = (resMult.mejoraInsertarPorcentaje + resDiv.mejoraInsertarPorcentaje) / 2.0;
        double promMejoraTiempoBor = (resMult.mejoraBorrarPorcentaje + resDiv.mejoraBorrarPorcentaje) / 2.0;
        double promMejoraPasosIns = (resMult.mejoraPasosInsertarPorcentaje + resDiv.mejoraPasosInsertarPorcentaje) / 2.0;
        double promMejoraPasosBor = (resMult.mejoraPasosBorrarPorcentaje + resDiv.mejoraPasosBorrarPorcentaje) / 2.0;

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================\n");
        sb.append(" COMPARATIVA DE RENDIMIENTO: TABLA HASH + LISTA SIMPLE vs. TABLA HASH + ÁRBOL BINARIO\n");
        sb.append("====================================================================================\n");
        sb.append(String.format(" Registros evaluados (n) : %d   |   Tamaño Tabla (m) : 256   |   Factor carga (α) : %.2f\n",
                resMult.cantidadRegistros, resMult.cantidadRegistros / 256.0));
        sb.append(" Iteraciones de prueba   : 200 repeticiones completas (con calentamiento JIT previo)\n\n");

        sb.append(" 1. MÉTODO DE LA MULTIPLICACIÓN  h(k) = ⌊256(kA mod 1)⌋\n");
        sb.append(String.format("    - Insertar : Lista = %6.1f ns/op (%.2f nodos) | Árbol = %6.1f ns/op (%.2f nodos) => Mejora: %+6.2f%% tiempo (%+5.2f%% nodos)\n",
                resMult.tiempoInsertarListaNs, resMult.pasosPromedioInsertarLista,
                resMult.tiempoInsertarArbolNs, resMult.pasosPromedioInsertarArbol,
                resMult.mejoraInsertarPorcentaje, resMult.mejoraPasosInsertarPorcentaje));
        sb.append(String.format("    - Borrar   : Lista = %6.1f ns/op (%.2f nodos) | Árbol = %6.1f ns/op (%.2f nodos) => Mejora: %+6.2f%% tiempo (%+5.2f%% nodos)\n",
                resMult.tiempoBorrarListaNs, resMult.pasosPromedioBorrarLista,
                resMult.tiempoBorrarArbolNs, resMult.pasosPromedioBorrarArbol,
                resMult.mejoraBorrarPorcentaje, resMult.mejoraPasosBorrarPorcentaje));
        sb.append(String.format("    - Buscar   : Lista = %6.1f ns/op              | Árbol = %6.1f ns/op              => Mejora: %+6.2f%% tiempo\n\n",
                resMult.tiempoBuscarListaNs, resMult.tiempoBuscarArbolNs, resMult.mejoraBuscarPorcentaje));

        sb.append(" 2. MÉTODO DE LA DIVISIÓN  h(k) = k mod 256\n");
        sb.append(String.format("    - Insertar : Lista = %6.1f ns/op (%.2f nodos) | Árbol = %6.1f ns/op (%.2f nodos) => Mejora: %+6.2f%% tiempo (%+5.2f%% nodos)\n",
                resDiv.tiempoInsertarListaNs, resDiv.pasosPromedioInsertarLista,
                resDiv.tiempoInsertarArbolNs, resDiv.pasosPromedioInsertarArbol,
                resDiv.mejoraInsertarPorcentaje, resDiv.mejoraPasosInsertarPorcentaje));
        sb.append(String.format("    - Borrar   : Lista = %6.1f ns/op (%.2f nodos) | Árbol = %6.1f ns/op (%.2f nodos) => Mejora: %+6.2f%% tiempo (%+5.2f%% nodos)\n",
                resDiv.tiempoBorrarListaNs, resDiv.pasosPromedioBorrarLista,
                resDiv.tiempoBorrarArbolNs, resDiv.pasosPromedioBorrarArbol,
                resDiv.mejoraBorrarPorcentaje, resDiv.mejoraPasosBorrarPorcentaje));
        sb.append(String.format("    - Buscar   : Lista = %6.1f ns/op              | Árbol = %6.1f ns/op              => Mejora: %+6.2f%% tiempo\n\n",
                resDiv.tiempoBuscarListaNs, resDiv.tiempoBuscarArbolNs, resDiv.mejoraBuscarPorcentaje));

        sb.append("====================================================================================\n");
        sb.append(" RESPUESTA CONSOLIDADA (PROMEDIO ENTRE AMBOS MÉTODOS HASH):\n");
        sb.append(String.format("  • Mejora porcentual en INSERTAR : %.2f%% en tiempo de respuesta (%.2f%% menos nodos recorridos)\n",
                promMejoraTiempoIns, promMejoraPasosIns));
        sb.append(String.format("  • Mejora porcentual en BORRAR   : %.2f%% en tiempo de respuesta (%.2f%% menos nodos recorridos)\n",
                promMejoraTiempoBor, promMejoraPasosBor));
        sb.append("====================================================================================\n");

        JTextArea area = new JTextArea(sb.toString(), 22, 88);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(248, 250, 252));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Resultados Benchmark: Tabla Hash + Lista vs. Tabla Hash + Árbol Binario",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
