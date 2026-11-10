package co.edu.unicartagena.hash.app;

import co.edu.unicartagena.hash.gui.VentanaPrincipal;
import co.edu.unicartagena.hash.model.BenchmarkHash;
import co.edu.unicartagena.hash.model.FuncionHash;
import co.edu.unicartagena.hash.model.Registro;
import co.edu.unicartagena.hash.model.TablaHashLista;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar la aplicación del Taller Tablas Hash.
 */
public class App {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--verify")) {
            verificarConsola();
            return;
        }
        if (args.length > 0 && args[0].equals("--capture")) {
            generarCapturas();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal panel = new VentanaPrincipal();
            JFrame frame = panel.crearFrame();
            frame.setVisible(true);
        });
    }

    private static void verificarConsola() {
        long k = FuncionHash.calcularLlaveRadix2("05-9165610");
        int h1 = FuncionHash.hashMultiplicacion(k);
        int h2 = FuncionHash.hashDivision(k);
        System.out.println("Verificación ejemplo PDF (05-9165610):");
        System.out.println("k = " + k + " | h1 (Multiplicación) = " + h1 + " | h2 (División) = " + h2);

        try {
            File csv = new File("MOCK_DATA.csv");
            TablaHashLista t1 = new TablaHashLista(FuncionHash.METODO_MULTIPLICACION);
            TablaHashLista t2 = new TablaHashLista(FuncionHash.METODO_DIVISION);
            t1.cargarArchivo(csv);
            t2.cargarArchivo(csv);
            System.out.println("Colisiones Multiplicación: " + t1.getColisiones());
            System.out.println("Colisiones División:       " + t2.getColisiones());

            List<Registro> todos = new ArrayList<>();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(csv))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Registro r = Registro.desdeCsv(linea);
                    if (r != null) {
                        todos.add(r);
                    }
                }
            }
            System.out.println("\n" + BenchmarkHash.compararRendimiento(todos, FuncionHash.METODO_MULTIPLICACION));
            System.out.println("\n" + BenchmarkHash.compararRendimiento(todos, FuncionHash.METODO_DIVISION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generarCapturas() {
        try {
            VentanaPrincipal v = new VentanaPrincipal();
            v.setSize(960, 640);

            v.configurarModo(FuncionHash.METODO_MULTIPLICACION, false, 149, "05-9165610");
            guardarImagen(v, "gui_metodo1_lista.png");

            v.configurarModo(FuncionHash.METODO_DIVISION, false, 82, "05-9165610");
            guardarImagen(v, "gui_metodo2_lista.png");

            v.configurarModo(FuncionHash.METODO_MULTIPLICACION, true, 149, "05-9165610");
            guardarImagen(v, "gui_metodo1_arbol.png");

            v.configurarModo(FuncionHash.METODO_DIVISION, true, 82, "05-9165610");
            guardarImagen(v, "gui_metodo2_arbol.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void guardarImagen(VentanaPrincipal panel, String archivo) throws Exception {
        layoutRecursivo(panel);
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        panel.paint(g2);
        g2.dispose();
        ImageIO.write(img, "png", new File(archivo));
    }

    private static void layoutRecursivo(Container c) {
        c.doLayout();
        for (Component hijo : c.getComponents()) {
            if (hijo instanceof Container) {
                layoutRecursivo((Container) hijo);
            }
        }
    }
}
