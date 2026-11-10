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
 * Tabla Hash de tamaño m = 256 donde cada slot es una Lista Enlazada Simple (Session 8).
 */
public class TablaHashLista {

    private Lista<Registro>[] tabla;
    private int metodoHash;

    @SuppressWarnings("unchecked")
    public TablaHashLista(int metodoHash) {
        this.metodoHash = metodoHash;
        this.tabla = new Lista[FuncionHash.M];
        for (int i = 0; i < FuncionHash.M; i++) {
            this.tabla[i] = new Lista<>();
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
        Lista<Registro> lista = tabla[indice];
        lista.eliminarDato(r);
        lista.adicionarFinal(new Nodo<>(r));
    }

    public Registro buscar(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }
        Registro muestra = new Registro(plate.trim());
        int indice = FuncionHash.calcularHash(muestra.getLlaveK(), metodoHash);
        return tabla[indice].buscar(muestra);
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
        return tabla[indice].eliminarDato(muestra);
    }

    /**
     * Nota 1 del PDF: "Por colisión se entiende el número de objetos que comparten
     * un mismo espacio (slot) en la tabla hash."
     */
    public int getColisiones() {
        int colisiones = 0;
        for (int i = 0; i < FuncionHash.M; i++) {
            int tam = tabla[i].getTamaño();
            if (tam > 1) {
                colisiones += (tam - 1);
            }
        }
        return colisiones;
    }

    public Lista<Registro> getSlot(int indice) {
        return tabla[indice];
    }

    public List<Registro> obtenerRegistrosSlot(int indice) {
        List<Registro> resultado = new ArrayList<>();
        Nodo<Registro> aux = tabla[indice].getPrimerNodo();
        while (aux != null) {
            resultado.add(aux.getDato());
            aux = aux.getSig();
        }
        return resultado;
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
                Nodo<Registro> aux = tabla[i].getPrimerNodo();
                while (aux != null) {
                    bw.write(aux.getDato().toCsv());
                    bw.newLine();
                    aux = aux.getSig();
                }
            }
        }
    }
}
