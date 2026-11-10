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

public class Node {
    public long datos;
    public String idClave;
    public Object elemento;
    public Node izquierdo;
    public Node derecho;

    public Node(long dato, Node izq, Node der) {
        this.datos = dato;
        this.idClave = String.valueOf(dato);
        this.elemento = null;
        this.izquierdo = izq;
        this.derecho = der;
    }

    public Node(long dato) {
        this.datos = dato;
        this.idClave = String.valueOf(dato);
        this.elemento = null;
        this.izquierdo = null;
        this.derecho = null;
    }

    public Node(long dato, String idClave, Object elemento) {
        this.datos = dato;
        this.idClave = idClave != null ? idClave : String.valueOf(dato);
        this.elemento = elemento;
        this.izquierdo = null;
        this.derecho = null;
    }

    public long getDato() {
        return this.datos;
    }

    public String getIdClave() {
        return this.idClave;
    }

    public Object getElemento() {
        return this.elemento;
    }

    public void setElemento(Object elemento) {
        this.elemento = elemento;
    }
}
