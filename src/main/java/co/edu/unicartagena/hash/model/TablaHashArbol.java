package co.edu.unicartagena.hash.model;

import co.edu.unicartagena.binarytree.BinaryTree;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla Hash de tamaño m = 256 donde cada slot almacena los registros mediante
 * un Árbol Binario de Búsqueda (co.edu.unicartagena.binarytree.BinaryTree).
 */
public class TablaHashArbol {

    public static final int M = FuncionHash.M;
    private final BinaryTree[] tabla;
    private FuncionHash.Metodo metodo;
    private int totalRegistros;

    public TablaHashArbol(FuncionHash.Metodo metodo) {
        this.metodo = metodo != null ? metodo : FuncionHash.Metodo.MULTIPLICACION;
        this.tabla = new BinaryTree[M];
        for (int i = 0; i < M; i++) {
            this.tabla[i] = new BinaryTree();
        }
        this.totalRegistros = 0;
    }

    public void limpiar() {
        for (int i = 0; i < M; i++) {
            this.tabla[i].vaciar();
        }
        this.totalRegistros = 0;
    }

    public FuncionHash.Metodo getMetodo() {
        return metodo;
    }

    public void setMetodo(FuncionHash.Metodo nuevoMetodo) {
        if (nuevoMetodo == null || nuevoMetodo == this.metodo) {
            return;
        }
        List<Registro> todos = obtenerTodosLosRegistros();
        this.metodo = nuevoMetodo;
        limpiar();
        for (Registro r : todos) {
            insertar(r);
        }
    }

    public int calcularSlot(long k) {
        return FuncionHash.calcularHash(k, metodo);
    }

    public int calcularSlot(String plate) {
        return calcularSlot(FuncionHash.calcularLlaveRadix2(plate));
    }

    /**
     * Inserta un registro en el Árbol Binario del slot correspondiente.
     */
    public boolean insertar(Registro registro) {
        if (registro == null || registro.getPlate().isEmpty()) {
            return false;
        }
        long k = registro.getK();
        int slot = calcularSlot(k);
        boolean esNuevo = tabla[slot].adicionarElemento(k, registro.getPlate(), registro);
        if (esNuevo) {
            totalRegistros++;
        }
        return esNuevo;
    }

    /**
     * Busca un registro por id (plate) en el Árbol Binario del slot correspondiente en tiempo O(log n_slot).
     */
    public Registro buscar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }
        String limpio = plate.trim();
        long k = FuncionHash.calcularLlaveRadix2(limpio);
        int slot = calcularSlot(k);
        Object encontrado = tabla[slot].buscarElemento(k, limpio);
        return (Registro) encontrado;
    }

    /**
     * Elimina un registro según el id (plate) del Árbol Binario en el slot correspondiente en tiempo O(log n_slot).
     */
    public boolean eliminar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        String limpio = plate.trim();
        long k = FuncionHash.calcularLlaveRadix2(limpio);
        int slot = calcularSlot(k);
        boolean eliminado = tabla[slot].eliminarElemento(k, limpio);
        if (eliminado) {
            totalRegistros--;
        }
        return eliminado;
    }

    public BinaryTree getSlot(int indice) {
        if (indice < 0 || indice >= M) {
            throw new IndexOutOfBoundsException("Slot fuera de rango: " + indice);
        }
        return tabla[indice];
    }

    public List<Registro> getRegistrosEnSlot(int indice) {
        List<Object> raw = getSlot(indice).obtenerElementosInOrder();
        List<Registro> resultado = new ArrayList<>(raw.size());
        for (Object obj : raw) {
            if (obj instanceof Registro) {
                resultado.add((Registro) obj);
            }
        }
        return resultado;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public int getNumeroColisiones() {
        int colisiones = 0;
        for (int i = 0; i < M; i++) {
            int tam = tabla[i].getNumeroNodos();
            if (tam > 1) {
                colisiones += (tam - 1);
            }
        }
        return colisiones;
    }

    public int getTotalObjetosEnSlotsConColision() {
        int total = 0;
        for (int i = 0; i < M; i++) {
            int tam = tabla[i].getNumeroNodos();
            if (tam > 1) {
                total += tam;
            }
        }
        return total;
    }

    public int getCantidadSlotsConColision() {
        int count = 0;
        for (int i = 0; i < M; i++) {
            if (tabla[i].getNumeroNodos() > 1) {
                count++;
            }
        }
        return count;
    }

    public int getCantidadSlotsOcupados() {
        int count = 0;
        for (int i = 0; i < M; i++) {
            if (tabla[i].getNumeroNodos() > 0) {
                count++;
            }
        }
        return count;
    }

    public List<Integer> getIndicesSlotsConMasDeUnRegistro() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            if (tabla[i].getNumeroNodos() > 1) {
                indices.add(i);
            }
        }
        return indices;
    }

    public List<Registro> obtenerTodosLosRegistros() {
        List<Registro> todos = new ArrayList<>(totalRegistros);
        for (int i = 0; i < M; i++) {
            todos.addAll(getRegistrosEnSlot(i));
        }
        return todos;
    }

    public int cargarDesdeCSV(File archivo) throws IOException {
        List<Registro> leidos = TablaHashLista.leerArchivoCSV(archivo);
        limpiar();
        int cargados = 0;
        for (Registro r : leidos) {
            insertar(r);
            cargados++;
        }
        return cargados;
    }

    public void guardarEstado(File archivoDestino) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivoDestino), StandardCharsets.UTF_8))) {
            bw.write("plate;first_name;last_name;email;gender;k;slot_hash");
            bw.newLine();
            for (int i = 0; i < M; i++) {
                for (Registro r : getRegistrosEnSlot(i)) {
                    bw.write(r.toCsvLine() + ";" + r.getK() + ";" + i);
                    bw.newLine();
                }
            }
        }
    }
}
