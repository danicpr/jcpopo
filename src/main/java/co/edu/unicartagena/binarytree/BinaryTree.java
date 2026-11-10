package co.edu.unicartagena.binarytree;

/**
 * Estructura de Datos - Árbol Binario de Búsqueda
 * Docente: Juan Carlos García-Ojeda
 */
public class BinaryTree {
    public Node raiz;
    public int nodos = 0;

    public BinaryTree() {
        limpiar();
    }

    public void limpiar() {
        this.raiz = null;
        this.nodos = 0;
    }

    public Node getRaiz() {
        return this.raiz;
    }

    public int getNumeroNodos() {
        return this.nodos;
    }

    // Inserta un nodo en el árbol binario ordenado por su llave k (datos)
    public void insertar(long valor, Object info) {
        if (this.raiz == null) {
            this.raiz = new Node(valor, info);
            this.nodos++;
            return;
        }
        Node actual = this.raiz;
        while (true) {
            if (valor < actual.datos) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = new Node(valor, info);
                    this.nodos++;
                    return;
                }
                actual = actual.izquierdo;
            } else if (valor > actual.datos) {
                if (actual.derecho == null) {
                    actual.derecho = new Node(valor, info);
                    this.nodos++;
                    return;
                }
                actual = actual.derecho;
            } else {
                if (actual.info != null && actual.info.equals(info)) {
                    actual.info = info;
                    return;
                }
                if (actual.derecho == null) {
                    actual.derecho = new Node(valor, info);
                    this.nodos++;
                    return;
                }
                actual = actual.derecho;
            }
        }
    }

    // Busca un objeto en el árbol guiándose por la llave numérica k (datos)
    public Object buscar(long valor, Object ejemplo) {
        Node actual = this.raiz;
        while (actual != null) {
            if (valor < actual.datos) {
                actual = actual.izquierdo;
            } else if (valor > actual.datos) {
                actual = actual.derecho;
            } else {
                if (actual.info != null && actual.info.equals(ejemplo)) {
                    return actual.info;
                }
                actual = actual.derecho;
            }
        }
        return null;
    }

    // Elimina un nodo del árbol binario de búsqueda guiándose por la llave k
    public boolean eliminar(long valor, Object ejemplo) {
        int antes = this.nodos;
        this.raiz = eliminarRecursivo(this.raiz, valor, ejemplo);
        return this.nodos < antes;
    }

    private Node eliminarRecursivo(Node actual, long valor, Object ejemplo) {
        if (actual == null) {
            return null;
        }
        if (valor < actual.datos) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, valor, ejemplo);
            return actual;
        }
        if (valor > actual.datos) {
            actual.derecho = eliminarRecursivo(actual.derecho, valor, ejemplo);
            return actual;
        }
        if (actual.info != null && actual.info.equals(ejemplo)) {
            this.nodos--;
            // Caso 1 y 2: hoja o un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Caso 3: dos hijos (reemplazar por el menor del subárbol derecho)
            Node sucesor = encontrarMinimo(actual.derecho);
            actual.datos = sucesor.datos;
            actual.info = sucesor.info;
            actual.derecho = eliminarMinimo(actual.derecho);
            return actual;
        }
        actual.derecho = eliminarRecursivo(actual.derecho, valor, ejemplo);
        return actual;
    }

    private Node encontrarMinimo(Node nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    private Node eliminarMinimo(Node nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return nodo;
    }

    public void inorder(Node r) {
        if (r != null) {
            inorder(r.izquierdo);
            System.out.print(r.getDato() + ", ");
            inorder(r.derecho);
        }
    }
}
