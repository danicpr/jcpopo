/*
Copyright (c) 2019 - 2026, Juan Carlos Garcia Ojeda, Universidad de Cartagena
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

package co.edu.unicartagena.list;

import java.util.ArrayList;
import java.util.List;

public class Lista<T> {
    Nodo<T> primerNodo;
    int tamaño = 0;

    public Lista() {
        limpiar();
    }

    private void limpiar() {
        primerNodo = null;
        tamaño = 0;
    }

    public void vaciar() {
        limpiar();
    }

    private boolean estaVacia() {
        if (tamaño == 0)
            return true;
        else
            return false;
    }

    public boolean esVacia() {
        return estaVacia();
    }

    public int getTamaño() {
        return tamaño;
    }

    public Nodo<T> getPrimerNodo() {
        return primerNodo;
    }

    public boolean adicionar(Nodo<T> Nodo) {
        // ADICIONAR AL COMIENZO DE LA LISTA
        Nodo<T> nuevoNodo = Nodo;
        if (estaVacia())
            primerNodo = nuevoNodo;
        else {
            nuevoNodo.sig = primerNodo;
            primerNodo = nuevoNodo;
        }
        tamaño = tamaño + 1;
        return true;
    }

    /*
     * adicionarFinal
     * 
     * adiciona un nodo al final de la lista
     */
    public boolean adicionarFinal(Nodo<T> Nodo) {
        Nodo<T> nuevoNodo = Nodo;
        Nodo<T> temp = null;
        if (estaVacia())
            primerNodo = nuevoNodo;
        else {
            temp = primerNodo;
            while (temp.sig != null)
                temp = temp.sig;
            temp.sig = nuevoNodo;
        }
        tamaño = tamaño + 1;
        return true;
    }

    public boolean eliminar() {
        if (estaVacia() || primerNodo == null) {
            return false;
        }
        Nodo<T> temp = primerNodo;
        primerNodo = temp.sig;
        tamaño = tamaño - 1;
        return true;
    }

    /**
     * Busca un elemento en la lista que sea igual (equals) al objetivo.
     */
    public T buscarElemento(T objetivo) {
        if (estaVacia() || objetivo == null) {
            return null;
        }
        Nodo<T> temp = primerNodo;
        while (temp != null) {
            if (objetivo.equals(temp.getDato())) {
                return temp.getDato();
            }
            temp = temp.sig;
        }
        return null;
    }

    /**
     * Reemplaza un elemento si ya existe en la lista (según equals), o lo adiciona al final si no existe.
     * Retorna true si se insertó un nuevo nodo, false si se actualizó uno existente.
     */
    public boolean insertarOActualizarFinal(T dato) {
        if (estaVacia()) {
            primerNodo = new Nodo<>(dato);
            tamaño = 1;
            return true;
        }
        Nodo<T> temp = primerNodo;
        while (true) {
            if (dato.equals(temp.getDato())) {
                temp.setDato(dato);
                return false;
            }
            if (temp.sig == null) {
                temp.sig = new Nodo<>(dato);
                tamaño = tamaño + 1;
                return true;
            }
            temp = temp.sig;
        }
    }

    /**
     * Elimina el primer nodo cuyo dato coincida con el objetivo (según equals).
     */
    public boolean eliminarElemento(T objetivo) {
        if (estaVacia() || primerNodo == null || objetivo == null) {
            return false;
        }
        if (objetivo.equals(primerNodo.getDato())) {
            primerNodo = primerNodo.sig;
            tamaño = tamaño - 1;
            return true;
        }
        Nodo<T> anterior = primerNodo;
        Nodo<T> actual = primerNodo.sig;
        while (actual != null) {
            if (objetivo.equals(actual.getDato())) {
                anterior.sig = actual.sig;
                tamaño = tamaño - 1;
                return true;
            }
            anterior = actual;
            actual = actual.sig;
        }
        return false;
    }

    public List<T> toList() {
        List<T> resultado = new ArrayList<>(tamaño);
        Nodo<T> temp = primerNodo;
        while (temp != null) {
            resultado.add(temp.getDato());
            temp = temp.sig;
        }
        return resultado;
    }

    /*
     * adicionarEntreNodos
     * 
     * adiciona un nodo entre nodos en orden no decreciente
     */
    public boolean adicionarEntreNodos(Nodo<T> Nodo) {
        Nodo<T> nuevoNodo = Nodo;
        Nodo<T> temp = null;
        Nodo<T> anterior = null;
        if (estaVacia())
            primerNodo = nuevoNodo;
        else {
            temp = primerNodo;
            anterior = temp;
            boolean band = false;
            // CASO 1 : Lista tiene un elemento
            if (tamaño == 1) {
                if (Integer.parseInt(temp.getDato().toString()) < Integer.parseInt(nuevoNodo.getDato().toString())) {
                    primerNodo.sig = nuevoNodo;
                } else {
                    nuevoNodo.sig = primerNodo;
                    primerNodo = nuevoNodo;
                }
            } else { // LISTA TIENE MAS DE UN ELEMENTO
                while (temp != null && Integer.parseInt(temp.getDato().toString()) < Integer.parseInt(nuevoNodo.getDato().toString())) {
                    anterior = temp;
                    temp = temp.sig;
                    band = true;
                }
                if (band) {
                    nuevoNodo.sig = anterior.sig;
                    anterior.sig = nuevoNodo;
                } else { // El valor del nuevo nodo es menor
                    nuevoNodo.sig = temp;
                    primerNodo = nuevoNodo;
                }
            }
        }
        tamaño = tamaño + 1;
        return true;
    }

    public void imprimir() {
        if (!estaVacia()) {
            Nodo<T> temp = primerNodo;
            while (temp != null) {
                System.out.println(temp.getDato());
                temp = temp.sig;
            }
        } else {
            System.out.println("La Lista esta vacia");
        }
    }
}
