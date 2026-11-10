package co.edu.unicartagena.list;

/**
 * Estructura de Datos - Sesión 8 (Lista Enlazada Simple)
 * Docente: Juan Carlos García-Ojeda
 */
public class Lista<T> {
    public Nodo<T> primerNodo;
    public int tamaño = 0;

    public Lista() {
        limpiar();
    }

    public void limpiar() {
        primerNodo = null;
        tamaño = 0;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Nodo<T> getPrimerNodo() {
        return primerNodo;
    }

    // Adiciona al comienzo de la lista
    public boolean adicionar(Nodo<T> nodo) {
        if (estaVacia()) {
            primerNodo = nodo;
            tamaño++;
            return true;
        } else {
            Nodo<T> aux = primerNodo;
            primerNodo = nodo;
            primerNodo.sig = aux;
            tamaño++;
            return true;
        }
    }

    // Adiciona al final de la lista
    public boolean adicionarFinal(Nodo<T> nodo) {
        if (estaVacia()) {
            primerNodo = nodo;
            tamaño++;
            return true;
        } else {
            Nodo<T> actual = primerNodo;
            while (actual.sig != null) {
                actual = actual.sig;
            }
            actual.sig = nodo;
            nodo.sig = null;
            tamaño++;
            return true;
        }
    }

    // Elimina el primer nodo de la lista
    public boolean eliminar() {
        if (!estaVacia()) {
            Nodo<T> actual = primerNodo;
            primerNodo = actual.sig;
            actual.sig = null;
            tamaño--;
            return true;
        }
        return false;
    }

    // Busca un dato en la lista usando equals
    public T buscar(T ejemplo) {
        Nodo<T> actual = primerNodo;
        while (actual != null) {
            if (actual.dato.equals(ejemplo)) {
                return actual.dato;
            }
            actual = actual.sig;
        }
        return null;
    }

    // Elimina un nodo especifico de la lista usando equals
    public boolean eliminarDato(T ejemplo) {
        if (estaVacia()) {
            return false;
        }
        if (primerNodo.dato.equals(ejemplo)) {
            primerNodo = primerNodo.sig;
            tamaño--;
            return true;
        }
        Nodo<T> anterior = primerNodo;
        Nodo<T> actual = primerNodo.sig;
        while (actual != null) {
            if (actual.dato.equals(ejemplo)) {
                anterior.sig = actual.sig;
                tamaño--;
                return true;
            }
            anterior = actual;
            actual = actual.sig;
        }
        return false;
    }

    public void imprimir() {
        if (!estaVacia()) {
            Nodo<T> actual = primerNodo;
            while (actual != null) {
                System.out.println(actual.getDato());
                actual = actual.sig;
            }
        }
    }
}
