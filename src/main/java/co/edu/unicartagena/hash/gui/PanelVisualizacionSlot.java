package co.edu.unicartagena.hash.gui;

import co.edu.unicartagena.binarytree.BinaryTree;
import co.edu.unicartagena.binarytree.Node;
import co.edu.unicartagena.hash.model.Registro;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JPanel;

/**
 * Panel gráfico en blanco y negro que dibuja los nodos del slot seleccionado
 * como Lista Enlazada Simple (igual al esquema del PDF) o como Árbol Binario.
 */
public class PanelVisualizacionSlot extends JPanel {

    private int slotSeleccionado = -1;
    private boolean modoArbol = false;
    private List<Registro> registrosLista;
    private BinaryTree arbolSlot;
    private String plateResaltado = null;

    public PanelVisualizacionSlot() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(650, 210));
    }

    public void mostrarSlotLista(int slot, List<Registro> registros, String resaltado) {
        this.slotSeleccionado = slot;
        this.modoArbol = false;
        this.registrosLista = registros;
        this.arbolSlot = null;
        this.plateResaltado = resaltado;
        int ancho = 120 + (registros != null ? registros.size() * 145 : 0);
        setPreferredSize(new Dimension(Math.max(650, ancho), 200));
        revalidate();
        repaint();
    }

    public void mostrarSlotArbol(int slot, BinaryTree arbol, String resaltado) {
        this.slotSeleccionado = slot;
        this.modoArbol = true;
        this.arbolSlot = arbol;
        this.registrosLista = null;
        this.plateResaltado = resaltado;
        setPreferredSize(new Dimension(650, 195));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);

        if (slotSeleccionado < 0) {
            g2.drawString("Seleccione un slot de la lista T para ver sus registros.", 20, 40);
            return;
        }

        if (!modoArbol) {
            dibujarListaEnlazada(g2);
        } else {
            dibujarArbolBinario(g2);
        }
    }

    private void dibujarListaEnlazada(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Slot T[" + slotSeleccionado + "] - Lista Enlazada Simple:", 15, 22);

        int xSlot = 20;
        int yCentro = 85;

        // Dibujar casilla del slot como en la Figura 1 y página 3 del PDF
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawRect(xSlot, yCentro - 36, 48, 24);
        g2.drawRect(xSlot, yCentro - 12, 48, 24);
        g2.drawRect(xSlot, yCentro + 12, 48, 24);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(String.valueOf(slotSeleccionado), xSlot + 12, yCentro + 5);

        if (registrosLista == null || registrosLista.isEmpty()) {
            return;
        }

        int xNodo = xSlot + 78;
        int anchoNodo = 115;
        int altoNodo = 36;
        int yNodo = yCentro - altoNodo / 2;

        // Flecha desde el slot al primer nodo
        dibujarFlecha(g2, xSlot + 48, yCentro, xNodo, yCentro);

        for (int i = 0; i < registrosLista.size(); i++) {
            Registro r = registrosLista.get(i);
            boolean resaltado = plateResaltado != null && plateResaltado.equalsIgnoreCase(r.getPlate());

            if (resaltado) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(xNodo, yNodo, anchoNodo, altoNodo);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRect(xNodo, yNodo, anchoNodo, altoNodo);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRect(xNodo, yNodo, anchoNodo, altoNodo);
            g2.setStroke(new BasicStroke(1.0f));

            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.drawString(r.getPlate(), xNodo + 12, yNodo + 16);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String nombre = r.getFirstName() + " " + r.getLastName();
            if (nombre.length() > 16) {
                nombre = nombre.substring(0, 16);
            }
            g2.drawString(nombre, xNodo + 10, yNodo + 30);

            if (i < registrosLista.size() - 1) {
                dibujarFlecha(g2, xNodo + anchoNodo, yCentro, xNodo + anchoNodo + 25, yCentro);
            }
            xNodo += anchoNodo + 25;
        }
    }

    private void dibujarArbolBinario(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Slot T[" + slotSeleccionado + "] - Árbol Binario de Búsqueda:", 15, 22);

        if (arbolSlot == null || arbolSlot.getRaiz() == null) {
            return;
        }
        int anchoPanel = Math.max(getWidth(), 640);
        dibujarNodoArbol(g2, arbolSlot.getRaiz(), anchoPanel / 2, 34, anchoPanel / 4);
    }

    private void dibujarNodoArbol(Graphics2D g2, Node nodo, int x, int y, int offsetH) {
        if (nodo == null) {
            return;
        }
        int sigY = y + 38;
        int sigOffset = Math.max(offsetH / 2, 42);

        if (nodo.izquierdo != null) {
            int izqX = x - offsetH;
            g2.drawLine(x, y + 12, izqX, sigY - 12);
            dibujarNodoArbol(g2, nodo.izquierdo, izqX, sigY, sigOffset);
        }
        if (nodo.derecho != null) {
            int derX = x + offsetH;
            g2.drawLine(x, y + 12, derX, sigY - 12);
            dibujarNodoArbol(g2, nodo.derecho, derX, sigY, sigOffset);
        }

        int ancho = 92;
        int alto = 24;
        int rx = x - ancho / 2;
        int ry = y - alto / 2;

        String texto = String.valueOf(nodo.datos);
        boolean resaltado = false;
        if (nodo.info instanceof Registro) {
            Registro r = (Registro) nodo.info;
            texto = r.getPlate();
            resaltado = plateResaltado != null && plateResaltado.equalsIgnoreCase(r.getPlate());
        }

        g2.setColor(resaltado ? Color.LIGHT_GRAY : Color.WHITE);
        g2.fillRect(rx, ry, ancho, alto);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(resaltado ? 2.0f : 1.0f));
        g2.drawRect(rx, ry, ancho, alto);
        g2.setStroke(new BasicStroke(1.0f));

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString(texto, rx + 8, ry + 16);
    }

    private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        int[] px = {x2, x2 - 6, x2 - 6};
        int[] py = {y2, y2 - 4, y2 + 4};
        g2.fillPolygon(px, py, 3);
    }
}
