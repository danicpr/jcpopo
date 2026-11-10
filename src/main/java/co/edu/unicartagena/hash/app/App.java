package co.edu.unicartagena.hash.app;

import co.edu.unicartagena.hash.gui.VentanaPrincipal;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.JPanel;
import co.edu.unicartagena.hash.model.BenchmarkHash;
import co.edu.unicartagena.hash.model.FuncionHash;
import co.edu.unicartagena.hash.model.Registro;
import co.edu.unicartagena.hash.model.TablaHashArbol;
import co.edu.unicartagena.hash.model.TablaHashLista;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación del Taller de Tablas Hash y Árboles Binarios.
 */
public class App {

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            if ("--verify".equals(args[0])) {
                ejecutarVerificacionConsola();
                return;
            }
            if ("--capture".equals(args[0])) {
                ejecutarCapturaPantallas();
                return;
            }
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Entorno sin servidor gráfico X11 detectado; ejecutando verificación por consola...");
            ejecutarVerificacionConsola();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.crearFrame().setVisible(true);
        });
    }

    private static void ejecutarVerificacionConsola() {
        System.out.println("=== 1. VERIFICACIÓN DE LLAVE RADIX-2 Y FUNCIONES HASH (EJEMPLO DEL ENUNCIADO) ===");
        Registro ejemplo = new Registro("05-9165610", "Letitia", "Maymand", "tres@x.com", "Female");
        long k = ejemplo.getK();
        int h1 = FuncionHash.hashMultiplicacion(k);
        int h2 = FuncionHash.hashDivision(k);
        System.out.println("Registro       : " + ejemplo.toCsvLine());
        System.out.println("Reducción ASCII: " + FuncionHash.explicarReduccionAscii(ejemplo.getPlate()));
        System.out.println("Llave k        : " + k + " (Esperado: 50770)");
        System.out.println("h(k) Método 1  : " + h1 + " (Esperado: 149)");
        System.out.println("h(k) Método 2  : " + h2 + " (Esperado: 82)");

        if (k != 50770L || h1 != 149 || h2 != 82) {
            throw new IllegalStateException("Error en la verificación matemática del ejemplo base.");
        }

        try {
            File csv = new File("MOCK_DATA.csv");
            System.out.println("\n=== 2. CARGA DE MOCK_DATA.csv Y CONTEO DE COLISIONES ===");
            for (FuncionHash.Metodo metodo : FuncionHash.Metodo.values()) {
                TablaHashLista tl = new TablaHashLista(metodo);
                TablaHashArbol ta = new TablaHashArbol(metodo);
                int n1 = tl.cargarDesdeCSV(csv);
                int n2 = ta.cargarDesdeCSV(csv);

                System.out.println("\n[" + metodo.getEtiqueta() + "]");
                System.out.println("  Registros cargados (Lista / Árbol) : " + n1 + " / " + n2);
                System.out.println("  Slots ocupados (|slot| >= 1)       : " + tl.getCantidadSlotsOcupados() + " de 256");
                System.out.println("  Slots con colisión (|slot| > 1)    : " + tl.getCantidadSlotsConColision() + " de 256");
                System.out.println("  Colisiones por exceso ∑(|slot|-1)  : " + tl.getNumeroColisiones());
                System.out.println("  Objetos en slots >1 ∑|slot|        : " + tl.getTotalObjetosEnSlotsConColision());

                Registro buscadoL = tl.buscar("05-9165610");
                Registro buscadoA = ta.buscar("05-9165610");
                if (buscadoL == null || buscadoA == null) {
                    throw new IllegalStateException("No se encontró 05-9165610 tras cargar CSV.");
                }
                Registro nuevo = new Registro("99-9999999", "Prueba", "Temporal", "test@unicartagena.edu.co", "Male");
                tl.insertar(nuevo);
                ta.insertar(nuevo);
                if (tl.buscar("99-9999999") == null || ta.buscar("99-9999999") == null) {
                    throw new IllegalStateException("Fallo al buscar registro recién insertado.");
                }
                if (!tl.eliminar("99-9999999") || !ta.eliminar("99-9999999")) {
                    throw new IllegalStateException("Fallo al eliminar registro recién insertado.");
                }
                if (tl.buscar("99-9999999") != null || ta.buscar("99-9999999") != null) {
                    throw new IllegalStateException("El registro eliminado sigue apareciendo en la tabla.");
                }
            }

            System.out.println("\n=== 3. BENCHMARK COMPARATIVO: LISTA ENLAZADA SIMPLE vs. ÁRBOL BINARIO ===");
            List<Registro> base = TablaHashLista.leerArchivoCSV(csv);
            int[] tamanos = {1000, 2500, 5000, 10000};
            for (int tam : tamanos) {
                List<Registro> datos = tam == 1000 ? base : BenchmarkHash.generarRegistrosSinteticos(tam);
                System.out.println("\n--- Escenario n = " + tam + " registros (Factor de carga α = " + String.format("%.2f", tam / 256.0) + ") ---");
                for (FuncionHash.Metodo metodo : FuncionHash.Metodo.values()) {
                    BenchmarkHash.ResultadoMetodo res = BenchmarkHash.ejecutarBenchmark(datos, metodo, 250);
                    System.out.printf("  %s:%n", metodo.name());
                    System.out.printf("    INSERTAR -> Lista: %7.1f ns (%.2f pasos) | Árbol: %7.1f ns (%.2f pasos) | Mejora Tiempo: %+6.2f%% | Mejora Pasos: %+6.2f%%%n",
                            res.tiempoInsertarListaNs, res.pasosPromedioInsertarLista,
                            res.tiempoInsertarArbolNs, res.pasosPromedioInsertarArbol,
                            res.mejoraInsertarPorcentaje, res.mejoraPasosInsertarPorcentaje);
                    System.out.printf("    BORRAR   -> Lista: %7.1f ns (%.2f pasos) | Árbol: %7.1f ns (%.2f pasos) | Mejora Tiempo: %+6.2f%% | Mejora Pasos: %+6.2f%%%n",
                            res.tiempoBorrarListaNs, res.pasosPromedioBorrarLista,
                            res.tiempoBorrarArbolNs, res.pasosPromedioBorrarArbol,
                            res.mejoraBorrarPorcentaje, res.mejoraPasosBorrarPorcentaje);
                    System.out.printf("    BUSCAR   -> Lista: %7.1f ns              | Árbol: %7.1f ns              | Mejora Tiempo: %+6.2f%%%n",
                            res.tiempoBuscarListaNs, res.tiempoBuscarArbolNs, res.mejoraBuscarPorcentaje);
                }
            }
            System.out.println("\nVERIFICACIÓN COMPLETA SIN ERRORES.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void ejecutarCapturaPantallas() {
        try {
            System.setProperty("java.awt.headless", "true");
            SwingUtilities.invokeAndWait(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    VentanaPrincipal ventana = new VentanaPrincipal();
                    JPanel contenedor = new JPanel(new BorderLayout());
                    if (ventana.getMenuBarPrincipal() != null) {
                        contenedor.add(ventana.getMenuBarPrincipal(), BorderLayout.NORTH);
                    }
                    contenedor.add(ventana, BorderLayout.CENTER);
                    contenedor.setSize(new Dimension(1160, 760));
                    layoutRecursivo(contenedor);

                    ventana.cambiarEstructuraSlot(false);
                    ventana.cambiarMetodoHash(FuncionHash.Metodo.MULTIPLICACION);
                    ventana.seleccionarSlotYPlaca(149, "05-9165610");
                    guardarImagenComponente(contenedor, new File("gui_metodo1_lista.png"));

                    ventana.cambiarEstructuraSlot(false);
                    ventana.cambiarMetodoHash(FuncionHash.Metodo.DIVISION);
                    ventana.seleccionarSlotYPlaca(82, "05-9165610");
                    guardarImagenComponente(contenedor, new File("gui_metodo2_lista.png"));

                    ventana.cambiarEstructuraSlot(true);
                    ventana.cambiarMetodoHash(FuncionHash.Metodo.MULTIPLICACION);
                    ventana.seleccionarSlotYPlaca(149, "05-9165610");
                    guardarImagenComponente(contenedor, new File("gui_metodo1_arbol.png"));

                    ventana.cambiarEstructuraSlot(true);
                    ventana.cambiarMetodoHash(FuncionHash.Metodo.DIVISION);
                    ventana.seleccionarSlotYPlaca(82, "05-9165610");
                    guardarImagenComponente(contenedor, new File("gui_metodo2_arbol.png"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Capturas de GUI generadas exitosamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void layoutRecursivo(java.awt.Container c) {
        for (java.awt.Component hijo : c.getComponents()) {
            if (hijo instanceof javax.swing.JScrollPane) {
                javax.swing.JScrollPane sp = (javax.swing.JScrollPane) hijo;
                sp.doLayout();
                if (sp.getViewport() != null) {
                    sp.getViewport().doLayout();
                    java.awt.Component view = sp.getViewport().getView();
                    if (view != null) {
                        Dimension pref = view.getPreferredSize();
                        int w = Math.max(sp.getViewport().getWidth(), pref != null ? pref.width : 0);
                        int h = Math.max(sp.getViewport().getHeight(), pref != null ? pref.height : 0);
                        view.setSize(w, h);
                    }
                }
            }
        }
        c.doLayout();
        for (java.awt.Component hijo : c.getComponents()) {
            if (hijo instanceof java.awt.Container) {
                layoutRecursivo((java.awt.Container) hijo);
            }
        }
    }

    private static void guardarImagenComponente(JPanel comp, File destino) throws Exception {
        comp.setSize(1160, 760);
        layoutRecursivo(comp);
        layoutRecursivo(comp);
        BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        comp.paint(g2);
        g2.dispose();
        ImageIO.write(img, "png", destino);
    }
}
