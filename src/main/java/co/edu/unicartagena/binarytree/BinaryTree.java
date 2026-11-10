/*
Copyright (c) 2021 - 2026, Juan Carlos Garcia Ojeda, Universidad de Cartagena
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package co.edu.unicartagena.binarytree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree {

    public Node raiz;
    public Node izquierdo;
    public Node derecho;
    public int altura = 0;
    public long numeroMenor = Long.MAX_VALUE;
    public long numeroMayor = 0;
    public int nivelNumeroMenor = 0;
    public int nivelNumeroMayor = 0;
    public int nivel = 0;
    public int nodos = 0;

    public void adicionar(long valor, Node raiz, int nivel) {
        if (raiz == null) {
            Node EX = new Node(valor);
            this.raiz = EX;
            if (EX.getDato() < numeroMenor) {
                numeroMenor = EX.getDato();
                nivelNumeroMenor = nivel;
            }
            if (EX.getDato() > numeroMayor) {
                numeroMayor = EX.getDato();
                nivelNumeroMayor = nivel;
            }
            if (nivel > altura) {
                altura = nivel;
            }
            nodos++;
        } else {
            if (valor < raiz.getDato()) {
                if (raiz.izquierdo == null) {
                    Node EX = new Node(valor);
                    raiz.izquierdo = EX;
                    nivel++;
                    if (EX.getDato() < numeroMenor) {
                        numeroMenor = EX.getDato();
                        nivelNumeroMenor = nivel;
                    }
                    if (EX.getDato() > numeroMayor) {
                        numeroMayor = EX.getDato();
                        nivelNumeroMayor = nivel;
                    }
                    if (nivel > altura) {
                        altura = nivel;
                    }
                    nodos++;
                } else {
                    nivel++;
                    adicionar(valor, raiz.izquierdo, nivel);
                }
            } else if (valor > raiz.getDato()) {
                if (raiz.derecho == null) {
                    Node EX = new Node(valor);
                    raiz.derecho = EX;
                    nivel++;
                    if (EX.getDato() < numeroMenor) {
                        numeroMenor = EX.getDato();
                        nivelNumeroMenor = nivel;
                    }
                    if (EX.getDato() > numeroMayor) {
                        numeroMayor = EX.getDato();
                        nivelNumeroMayor = nivel;
                    }
                    if (nivel > altura) {
                        altura = nivel;
                    }
                    nodos++;
                } else {
                    nivel++;
                    adicionar(valor, raiz.derecho, nivel);
                }
            }
        }
    }

    /**
     * Compara un par (k, idClave) contra un nodo del ABB.
     * Ordena primero por la llave radix-2 (k = datos) y desempata lexicográficamente por idClave (plate).
     */
    private int comparar(long k, String idClave, Node nodo) {
        if (k < nodo.datos) {
            return -1;
        }
        if (k > nodo.datos) {
            return 1;
        }
        if (idClave == null || nodo.idClave == null) {
            return 0;
        }
        return idClave.compareTo(nodo.idClave);
    }

    /**
     * Inserta o actualiza un elemento con llave radix-2 k e identificador idClave en el Árbol Binario de Búsqueda.
     * Retorna true si se insertó un nuevo nodo, false si se actualizó uno existente.
     */
    public boolean adicionarElemento(long valor, String idClave, Object elemento) {
        if (this.raiz == null) {
            this.raiz = new Node(valor, idClave, elemento);
            this.nodos++;
            return true;
        }
        Node actual = this.raiz;
        while (true) {
            int cmp = comparar(valor, idClave, actual);
            if (cmp == 0) {
                actual.datos = valor;
                actual.idClave = idClave;
                actual.elemento = elemento;
                return false;
            } else if (cmp < 0) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = new Node(valor, idClave, elemento);
                    this.nodos++;
                    return true;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = new Node(valor, idClave, elemento);
                    this.nodos++;
                    return true;
                }
                actual = actual.derecho;
            }
        }
    }

    private void actualizarEstadisticasInsercion(long valor, int niv) {
        if (valor < numeroMenor) {
            numeroMenor = valor;
            nivelNumeroMenor = niv;
        }
        if (valor > numeroMayor) {
            numeroMayor = valor;
            nivelNumeroMayor = niv;
        }
        if (niv > altura) {
            altura = niv;
        }
    }

    /**
     * Busca un elemento en el ABB en tiempo O(h) usando su llave radix-2 k e idClave.
     */
    public Object buscarElemento(long valor, String idClave) {
        Node actual = this.raiz;
        while (actual != null) {
            int cmp = comparar(valor, idClave, actual);
            if (cmp == 0) {
                return actual.elemento;
            } else if (cmp < 0) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }
        return null;
    }

    /**
     * Elimina un nodo del ABB según su llave radix-2 k e idClave en tiempo O(h).
     * Retorna true si el nodo existía y fue eliminado.
     */
    private boolean flagEliminado = false;

    public boolean eliminarElemento(long valor, String idClave) {
        this.flagEliminado = false;
        this.raiz = eliminarRecursivo(this.raiz, valor, idClave);
        if (this.flagEliminado) {
            this.nodos--;
        }
        return this.flagEliminado;
    }

    private Node eliminarRecursivo(Node actual, long valor, String idClave) {
        if (actual == null) {
            return null;
        }
        int cmp = comparar(valor, idClave, actual);
        if (cmp < 0) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, valor, idClave);
        } else if (cmp > 0) {
            actual.derecho = eliminarRecursivo(actual.derecho, valor, idClave);
        } else {
            this.flagEliminado = true;
            // Caso 1 y 2: hoja o un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Caso 3: dos hijos -> reemplazar por el sucesor in-order (mínimo del subárbol derecho)
            Node sucesor = encontrarMinimo(actual.derecho);
            actual.datos = sucesor.datos;
            actual.idClave = sucesor.idClave;
            actual.elemento = sucesor.elemento;
            actual.derecho = eliminarMinimo(actual.derecho);
        }
        return actual;
    }

    private Node eliminarMinimo(Node nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return nodo;
    }

    private Node encontrarMinimo(Node nodo) {
        Node actual = nodo;
        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    private int calcularAltura(Node nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.izquierdo == null && nodo.derecho == null) {
            return 0;
        }
        return 1 + Math.max(calcularAltura(nodo.izquierdo), calcularAltura(nodo.derecho));
    }

    public void vaciar() {
        this.raiz = null;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 0;
        this.nodos = 0;
        this.numeroMenor = Long.MAX_VALUE;
        this.numeroMayor = 0;
        this.nivelNumeroMenor = 0;
        this.nivelNumeroMayor = 0;
    }

    public List<Object> obtenerElementosInOrder() {
        List<Object> lista = new ArrayList<>(this.nodos);
        recolectarInOrder(this.raiz, lista);
        return lista;
    }

    private void recolectarInOrder(Node nodo, List<Object> acumulador) {
        if (nodo != null) {
            recolectarInOrder(nodo.izquierdo, acumulador);
            if (nodo.elemento != null) {
                acumulador.add(nodo.elemento);
            }
            recolectarInOrder(nodo.derecho, acumulador);
        }
    }

    public int getNumeroNodos() {
        return this.nodos;
    }

    public int getAltura() {
        this.altura = calcularAltura(this.raiz);
        return this.altura;
    }

    public void inorder(Node raiz) {
        if (raiz != null) {
            inorder(raiz.izquierdo);
            System.out.print(raiz.getDato() + " ");
            inorder(raiz.derecho);
        }
    }

    public void postorder(Node raiz) {
        if (raiz != null) {
            postorder(raiz.izquierdo);
            postorder(raiz.derecho);
            System.out.print(raiz.getDato() + " ");
        }
    }

    public void preorder(Node raiz) {
        if (raiz != null) {
            System.out.print(raiz.getDato() + " ");
            preorder(raiz.izquierdo);
            preorder(raiz.derecho);
        }
    }

    public Node getRaiz() {
        return this.raiz;
    }
}
