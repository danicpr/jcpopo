package co.edu.unicartagena.binarytree;

/**
 * Estructura de Datos - Árbol Binario
 * Docente: Juan Carlos García-Ojeda
 */
public class Node {
    public long datos;
    public Object info;
    public Node izquierdo;
    public Node derecho;

    public Node(long dato, Node izq, Node der) {
        this.datos = dato;
        this.izquierdo = izq;
        this.derecho = der;
    }

    public Node(long dato) {
        this.datos = dato;
        this.izquierdo = null;
        this.derecho = null;
    }

    public Node(long dato, Object info) {
        this.datos = dato;
        this.info = info;
        this.izquierdo = null;
        this.derecho = null;
    }

    public long getDato() {
        return this.datos;
    }
}
