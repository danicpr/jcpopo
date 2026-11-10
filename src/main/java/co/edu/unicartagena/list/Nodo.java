package co.edu.unicartagena.list;

/**
 * Estructura de Datos - Sesión 8 (Lista Enlazada Simple)
 * Docente: Juan Carlos García-Ojeda
 */
public class Nodo<T> {
    public T dato;
    public Nodo<T> sig;

    public Nodo(T dato) {
        this.dato = dato;
        this.sig = null;
    }

    public Nodo(T dato, Nodo<T> sig) {
        this.dato = dato;
        this.sig = sig;
    }

    public T getDato() {
        return this.dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public Nodo<T> getSig() {
        return this.sig;
    }
}
