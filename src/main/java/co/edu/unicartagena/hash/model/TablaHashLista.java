package co.edu.unicartagena.hash.model;

import co.edu.unicartagena.list.Lista;
import co.edu.unicartagena.list.Nodo;
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
 * Tabla Hash de tamaño m = 256 donde cada slot almacena colisiones mediante
 * la Lista Enlazada Simple (co.edu.unicartagena.list.Lista) de la Sesión 8.
 */
public class TablaHashLista {

    public static final int M = FuncionHash.M;
    private final Lista<Registro>[] tabla;
    private FuncionHash.Metodo metodo;
    private int totalRegistros;

    @SuppressWarnings("unchecked")
    public TablaHashLista(FuncionHash.Metodo metodo) {
        this.metodo = metodo != null ? metodo : FuncionHash.Metodo.MULTIPLICACION;
        this.tabla = (Lista<Registro>[]) new Lista[M];
        for (int i = 0; i < M; i++) {
            this.tabla[i] = new Lista<>();
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
     * Inserta un registro en el slot correspondiente usando la Lista Enlazada Simple.
     * Si el id (plate) ya existe en el slot, actualiza sus datos; si no, lo adiciona al final de la lista.
     */
    public boolean insertar(Registro registro) {
        if (registro == null || registro.getPlate().isEmpty()) {
            return false;
        }
        int slot = calcularSlot(registro.getK());
        boolean esNuevo = tabla[slot].insertarOActualizarFinal(registro);
        if (esNuevo) {
            totalRegistros++;
        }
        return esNuevo;
    }

    /**
     * Inserta directamente al final usando adicionarFinal(new Nodo<>(registro)) de Lista.
     * Útil en pruebas de carga masiva cuando se sabe que las placas son únicas.
     */
    public void insertarDirectoFinal(Registro registro) {
        int slot = calcularSlot(registro.getK());
        tabla[slot].adicionarFinal(new Nodo<>(registro));
        totalRegistros++;
    }

    /**
     * Busca un registro según su id (plate).
     */
    public Registro buscar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }
        String limpio = plate.trim();
        int slot = calcularSlot(limpio);
        return tabla[slot].buscarElemento(new Registro(limpio));
    }

    /**
     * Borra un registro según su id (plate).
     */
    public boolean eliminar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        String limpio = plate.trim();
        int slot = calcularSlot(limpio);
        boolean eliminado = tabla[slot].eliminarElemento(new Registro(limpio));
        if (eliminado) {
            totalRegistros--;
        }
        return eliminado;
    }

    public Lista<Registro> getSlot(int indice) {
        if (indice < 0 || indice >= M) {
            throw new IndexOutOfBoundsException("Slot fuera de rango: " + indice);
        }
        return tabla[indice];
    }

    public List<Registro> getRegistrosEnSlot(int indice) {
        return getSlot(indice).toList();
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    /**
     * Número de colisiones: cantidad de objetos adicionales que colisionan en un mismo slot
     * (sumatoria de (|slot| - 1) para todo slot con |slot| > 1).
     */
    public int getNumeroColisiones() {
        int colisiones = 0;
        for (int i = 0; i < M; i++) {
            int tam = tabla[i].getTamaño();
            if (tam > 1) {
                colisiones += (tam - 1);
            }
        }
        return colisiones;
    }

    /**
     * Total de objetos almacenados en slots que presentan colisión (|slot| > 1).
     * Corresponde literalmente al total de objetos que comparten un mismo espacio con al menos otro objeto.
     */
    public int getTotalObjetosEnSlotsConColision() {
        int total = 0;
        for (int i = 0; i < M; i++) {
            int tam = tabla[i].getTamaño();
            if (tam > 1) {
                total += tam;
            }
        }
        return total;
    }

    /**
     * Cantidad de espacios (slots) que tienen más de un registro (|slot| > 1).
     */
    public int getCantidadSlotsConColision() {
        int count = 0;
        for (int i = 0; i < M; i++) {
            if (tabla[i].getTamaño() > 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Cantidad de espacios (slots) ocupados (|slot| >= 1).
     */
    public int getCantidadSlotsOcupados() {
        int count = 0;
        for (int i = 0; i < M; i++) {
            if (tabla[i].getTamaño() > 0) {
                count++;
            }
        }
        return count;
    }

    public List<Integer> getIndicesSlotsConMasDeUnRegistro() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            if (tabla[i].getTamaño() > 1) {
                indices.add(i);
            }
        }
        return indices;
    }

    public List<Registro> obtenerTodosLosRegistros() {
        List<Registro> todos = new ArrayList<>(totalRegistros);
        for (int i = 0; i < M; i++) {
            Nodo<Registro> actual = tabla[i].getPrimerNodo();
            while (actual != null) {
                todos.add(actual.getDato());
                actual = actual.getSig();
            }
        }
        return todos;
    }

    /**
     * Carga registros desde un archivo CSV con formato:
     * plate ; first_name ; last_name ; email; gender
     */
    public int cargarDesdeCSV(File archivo) throws IOException {
        List<Registro> leidos = leerArchivoCSV(archivo);
        limpiar();
        int cargados = 0;
        for (Registro r : leidos) {
            insertar(r);
            cargados++;
        }
        return cargados;
    }

    public static List<Registro> leerArchivoCSV(File archivo) throws IOException {
        List<Registro> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                String limpia = linea.trim();
                if (limpia.isEmpty()) {
                    continue;
                }
                if (primeraLinea && limpia.startsWith("﻿")) {
                    limpia = limpia.substring(1).trim();
                }
                String[] partes = limpia.contains(";") ? limpia.split(";", -1) : limpia.split(",", -1);
                if (primeraLinea) {
                    primeraLinea = false;
                    if (partes[0].trim().equalsIgnoreCase("plate") || partes[0].trim().equalsIgnoreCase("id")) {
                        continue;
                    }
                }
                if (partes.length >= 5) {
                    lista.add(new Registro(partes[0], partes[1], partes[2], partes[3], partes[4]));
                } else if (partes.length >= 1 && !partes[0].trim().isEmpty()) {
                    String fn = partes.length > 1 ? partes[1] : "";
                    String ln = partes.length > 2 ? partes[2] : "";
                    String em = partes.length > 3 ? partes[3] : "";
                    String ge = partes.length > 4 ? partes[4] : "";
                    lista.add(new Registro(partes[0], fn, ln, em, ge));
                }
            }
        }
        return lista;
    }

    /**
     * Guarda el estado actual de la Tabla Hash en un archivo.
     * Si el nombre termina en .txt genera el reporte detallado por slot; si termina en .csv
     * guarda los registros actuales en formato CSV e incluye también un archivo .estado.txt.
     */
    public void guardarEstado(File archivoDestino) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivoDestino), StandardCharsets.UTF_8))) {
            if (archivoDestino.getName().toLowerCase().endsWith(".txt")) {
                escribirReporteDetallado(bw);
            } else {
                bw.write("plate;first_name;last_name;email;gender;k;slot_hash");
                bw.newLine();
                for (int i = 0; i < M; i++) {
                    Nodo<Registro> actual = tabla[i].getPrimerNodo();
                    while (actual != null) {
                        Registro r = actual.getDato();
                        bw.write(r.toCsvLine() + ";" + r.getK() + ";" + i);
                        bw.newLine();
                        actual = actual.getSig();
                    }
                }
            }
        }
    }

    private void escribirReporteDetallado(BufferedWriter bw) throws IOException {
        bw.write("====================================================================");
        bw.newLine();
        bw.write("ESTADO ACTUAL DE LA TABLA HASH (ESTRUCTURA: LISTA ENLAZADA SIMPLE)");
        bw.newLine();
        bw.write("Función Hash activa : " + metodo.getEtiqueta());
        bw.newLine();
        bw.write("Tamaño de tabla (m) : " + M);
        bw.newLine();
        bw.write("Total registros (n) : " + totalRegistros);
        bw.newLine();
        bw.write("Colisiones (exceso) : " + getNumeroColisiones());
        bw.newLine();
        bw.write("Objetos compartidos : " + getTotalObjetosEnSlotsConColision());
        bw.newLine();
        bw.write("Slots con >1 reg.   : " + getCantidadSlotsConColision());
        bw.newLine();
        bw.write("====================================================================");
        bw.newLine();
        for (int i = 0; i < M; i++) {
            int tam = tabla[i].getTamaño();
            if (tam > 0) {
                bw.write(String.format("Slot [%3d] (%d reg.): ", i, tam));
                Nodo<Registro> actual = tabla[i].getPrimerNodo();
                boolean primero = true;
                while (actual != null) {
                    if (!primero) {
                        bw.write(" -> ");
                    }
                    bw.write(actual.getDato().getPlate() + " (" + actual.getDato().getFirstName() + " " + actual.getDato().getLastName() + ")");
                    primero = false;
                    actual = actual.getSig();
                }
                bw.newLine();
            }
        }
    }
}
