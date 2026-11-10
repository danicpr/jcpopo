package co.edu.unicartagena.hash.model;

import co.edu.unicartagena.binarytree.BinaryTree;
import co.edu.unicartagena.binarytree.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parte 2: Tabla Hash de tamaño m = 256 donde cada slot almacena los registros
 * en un Árbol Binario de Búsqueda (BinaryTree).
 */
public class TablaHashArbol {

    private BinaryTree[] tabla;
    private int metodoHash;

    public TablaHashArbol(int metodoHash) {
        this.metodoHash = metodoHash;
        this.tabla = new BinaryTree[FuncionHash.M];
        for (int i = 0; i < FuncionHash.M; i++) {
            this.tabla[i] = new BinaryTree();
        }
    }

    public void limpiar() {
        for (int i = 0; i < FuncionHash.M; i++) {
            tabla[i].limpiar();
        }
    }

    public void insertar(Registro r) {
        if (r == null || r.getPlate().isEmpty()) {
            return;
        }
        int indice = FuncionHash.calcularHash(r.getLlaveK(), metodoHash);
        tabla[indice].insertar(r.getLlaveK(), r);
    }

    public Registro buscar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }
        Registro muestra = new Registro(plate.trim());
        int indice = FuncionHash.calcularHash(muestra.getLlaveK(), metodoHash);
        return (Registro) tabla[indice].buscar(muestra.getLlaveK(), muestra);
    }

    public boolean eliminar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        return eliminar(new Registro(plate.trim()));
    }

    public boolean eliminar(Registro muestra) {
        if (muestra == null) {
            return false;
        }
        int indice = FuncionHash.calcularHash(muestra.getLlaveK(), metodoHash);
        return tabla[indice].eliminar(muestra.getLlaveK(), muestra);
    }

    public int getColisiones() {
        int colisiones = 0;
        for (int i = 0; i < FuncionHash.M; i++) {
            int n = tabla[i].getNumeroNodos();
            if (n > 1) {
                colisiones += (n - 1);
            }
        }
        return colisiones;
    }

    public BinaryTree getSlot(int indice) {
        return tabla[indice];
    }

    public List<Registro> obtenerRegistrosSlot(int indice) {
        List<Registro> lista = new ArrayList<>();
        recorridoInOrder(tabla[indice].getRaiz(), lista);
        return lista;
    }

    private void recorridoInOrder(Node nodo, List<Registro> lista) {
        if (nodo != null) {
            recorridoInOrder(nodo.izquierdo, lista);
            if (nodo.info instanceof Registro) {
                lista.add((Registro) nodo.info);
            }
            recorridoInOrder(nodo.derecho, lista);
        }
    }

    public void cargarArchivo(File archivo) throws IOException {
        limpiar();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Registro r = Registro.desdeCsv(linea);
                if (r != null) {
                    insertar(r);
                }
            }
        }
    }

    public void guardarArchivo(File archivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            for (int i = 0; i < FuncionHash.M; i++) {
                List<Registro> regs = obtenerRegistrosSlot(i);
                for (Registro r : regs) {
                    bw.write(r.toCsv());
                    bw.newLine();
                }
            }
        }
    }
}
