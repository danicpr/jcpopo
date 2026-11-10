package co.edu.unicartagena.hash.gui;

import co.edu.unicartagena.binarytree.BinaryTree;
import co.edu.unicartagena.binarytree.Node;
import co.edu.unicartagena.hash.model.Registro;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

/**
 * Panel gráfico que dibuja la representación visual del slot seleccionado en la Tabla Hash T:
 * - En modo Lista Enlazada Simple: dibuja el vector vertical T[slot] -> [Placa 1] -> [Placa 2] -> ...
 *   tal como se especifica en la Figura 1 y la página 3 del enunciado.
 * - En modo Árbol Binario: dibuja el Árbol Binario de Búsqueda almacenado en T[slot].
 */
public class PanelVisualizacionSlot extends JPanel {

    private int slotSeleccionado = -1;
    private List<Registro> registrosSlot;
    private BinaryTree arbolSlot;
    private boolean modoArbol = false;
    private String placaResaltada = null;

    public PanelVisualizacionSlot() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(780, 230));
    }

    public void actualizarVistaLista(int slot, List<Registro> registros, String placaResaltada) {
        this.slotSeleccionado = slot;
        this.registrosSlot = registros;
        this.arbolSlot = null;
        this.modoArbol = false;
        this.placaResaltada = placaResaltada;
        int anchoNecesario = 160 + (registros != null ? registros.size() * 175 : 0);
        setPreferredSize(new Dimension(Math.max(760, anchoNecesario), 220));
        revalidate();
        repaint();
    }

    public void actualizarVistaArbol(int slot, BinaryTree arbol, List<Registro> registros, String placaResaltada) {
        this.slotSeleccionado = slot;
        this.arbolSlot = arbol;
        this.registrosSlot = registros;
        this.modoArbol = true;
        this.placaResaltada = placaResaltada;
        int alturaArbol = arbol != null ? arbol.getAltura() : 0;
        int anchoNecesario = Math.max(660, (1 << Math.min(alturaArbol + 1, 5)) * 105);
        int altoNecesario = Math.max(200, (alturaArbol + 1) * 56 + 44);
        setPreferredSize(new Dimension(anchoNecesario, altoNecesario));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (slotSeleccionado < 0) {
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2.drawString("Seleccione un slot en la columna T para visualizar su estructura interna.", 25, 45);
            g2.dispose();
            return;
        }

        if (!modoArbol) {
            dibujarListaEnlazada(g2);
        } else {
            dibujarArbolBinario(g2);
        }
        g2.dispose();
    }

    private void dibujarListaEnlazada(Graphics2D g2) {
        // Encabezado del diagrama
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        int cant = registrosSlot != null ? registrosSlot.size() : 0;
        g2.drawString("Estructura en T[" + slotSeleccionado + "] — Lista Enlazada Simple (" + cant + " registros):", 20, 24);

        // Dibujar columna vertical de slots alrededor del slot seleccionado (estilo página 3 del PDF)
        int slotX = 25;
        int slotW = 64;
        int slotH = 28;
        int centerY = 105;

        for (int offset = -2; offset <= 2; offset++) {
            int s = slotSeleccionado + offset;
            int y = centerY + offset * slotH;
            if (offset == 0) {
                g2.setColor(new Color(219, 234, 254));
                g2.fillRect(slotX, y, slotW, slotH);
                g2.setColor(new Color(29, 78, 216));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRect(slotX, y, slotW, slotH);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                String txt = String.valueOf(slotSeleccionado);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(txt, slotX + (slotW - fm.stringWidth(txt)) / 2, y + 19);
            } else {
                g2.setColor(new Color(248, 250, 252));
                g2.fillRect(slotX, y, slotW, slotH);
                g2.setColor(new Color(148, 163, 184));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRect(slotX, y, slotW, slotH);
                if (s >= 0 && s < 256) {
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    String txt = String.valueOf(s);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(txt, slotX + (slotW - fm.stringWidth(txt)) / 2, y + 18);
                }
            }
        }

        if (registrosSlot == null || registrosSlot.isEmpty()) {
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2.drawString("───► null (Slot vacío)", slotX + slotW + 15, centerY + 19);
            return;
        }

        int currX = slotX + slotW;
        int arrowLen = 34;
        int boxW = 138;
        int boxH = 52;
        int boxY = centerY + (slotH - boxH) / 2;

        for (int i = 0; i < registrosSlot.size(); i++) {
            Registro r = registrosSlot.get(i);
            int arrowStartX = currX;
            int arrowEndX = currX + arrowLen;
            int arrowY = centerY + slotH / 2;

            // Dibujar flecha
            g2.setColor(new Color(15, 23, 42));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(arrowStartX, arrowY, arrowEndX, arrowY);
            int[] px = {arrowEndX, arrowEndX - 8, arrowEndX - 8};
            int[] py = {arrowY, arrowY - 5, arrowY + 5};
            g2.fillPolygon(px, py, 3);

            int nodeX = arrowEndX;
            boolean esResaltado = placaResaltada != null && placaResaltada.equalsIgnoreCase(r.getPlate());

            // Caja del Nodo
            if (esResaltado) {
                g2.setColor(new Color(254, 240, 138));
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.fillRect(nodeX, boxY, boxW, boxH);

            g2.setColor(esResaltado ? new Color(202, 138, 4) : new Color(15, 23, 42));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRect(nodeX, boxY, boxW, boxH);

            // Línea divisoria del puntero sig
            int ptrX = nodeX + boxW - 18;
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(ptrX, boxY, ptrX, boxY + boxH);

            // Placa en negrita (como 05-9165610 en página 3)
            g2.setColor(new Color(15, 23, 42));
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            String plateTxt = r.getPlate();
            FontMetrics fmP = g2.getFontMetrics();
            int textAreaW = boxW - 18;
            g2.drawString(plateTxt, nodeX + Math.max(4, (textAreaW - fmP.stringWidth(plateTxt)) / 2), boxY + 18);

            // Nombre y k
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(51, 65, 85));
            String nombre = r.getFirstName() + " " + r.getLastName();
            if (nombre.length() > 17) {
                nombre = nombre.substring(0, 16) + ".";
            }
            g2.drawString(nombre, nodeX + 6, boxY + 33);

            g2.setColor(new Color(37, 99, 235));
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.drawString("k=" + r.getK(), nodeX + 6, boxY + 46);

            // Punto central en celda de puntero sig
            g2.setColor(new Color(15, 23, 42));
            g2.fillOval(ptrX + 6, arrowY - 3, 6, 6);

            currX = nodeX + boxW;
        }

        // Flecha final a null
        int arrowY = centerY + slotH / 2;
        g2.setColor(new Color(100, 116, 139));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(currX, arrowY, currX + 22, arrowY);
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("null", currX + 26, arrowY + 4);
    }

    private void dibujarArbolBinario(Graphics2D g2) {
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        int cant = arbolSlot != null ? arbolSlot.getNumeroNodos() : 0;
        int alt = arbolSlot != null ? arbolSlot.getAltura() : 0;
        g2.drawString("Estructura en T[" + slotSeleccionado + "] — Árbol Binario de Búsqueda (Nodos: " + cant + ", Altura: " + alt + "):", 20, 24);

        if (arbolSlot == null || arbolSlot.getRaiz() == null) {
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2.drawString("Árbol vacío (raiz = null)", 25, 60);
            return;
        }

        int anchoPanel = Math.max(getWidth(), 640);
        dibujarNodoArbol(g2, arbolSlot.getRaiz(), anchoPanel / 2, 46, Math.max(anchoPanel / 4, 85));
    }

    private void dibujarNodoArbol(Graphics2D g2, Node nodo, int x, int y, int offsetH) {
        if (nodo == null) {
            return;
        }
        int nextY = y + 54;
        int childOffset = Math.max(offsetH / 2, 58);

        g2.setStroke(new BasicStroke(1.8f));
        g2.setColor(new Color(100, 116, 139));
        if (nodo.izquierdo != null) {
            int leftX = x - offsetH;
            g2.drawLine(x, y + 18, leftX, nextY - 12);
            dibujarNodoArbol(g2, nodo.izquierdo, leftX, nextY, childOffset);
        }
        if (nodo.derecho != null) {
            int rightX = x + offsetH;
            g2.drawLine(x, y + 18, rightX, nextY - 12);
            dibujarNodoArbol(g2, nodo.derecho, rightX, nextY, childOffset);
        }

        int boxW = 112;
        int boxH = 38;
        int bx = x - boxW / 2;
        int by = y - boxH / 2;

        String id = nodo.getIdClave();
        boolean esResaltado = placaResaltada != null && id != null && placaResaltada.equalsIgnoreCase(id);

        if (esResaltado) {
            g2.setColor(new Color(254, 240, 138));
        } else {
            g2.setColor(new Color(239, 246, 255));
        }
        g2.fillRoundRect(bx, by, boxW, boxH, 10, 10);

        g2.setColor(esResaltado ? new Color(202, 138, 4) : new Color(29, 78, 216));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(bx, by, boxW, boxH, 10, 10);

        g2.setColor(new Color(15, 23, 42));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String txtId = id != null ? id : "";
        g2.drawString(txtId, bx + (boxW - fm.stringWidth(txtId)) / 2, by + 16);

        g2.setColor(new Color(37, 99, 235));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        String txtK = "k=" + nodo.getDato();
        FontMetrics fmK = g2.getFontMetrics();
        g2.drawString(txtK, bx + (boxW - fmK.stringWidth(txtK)) / 2, by + 31);
    }
}
