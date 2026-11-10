package co.edu.unicartagena.hash.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Parte 2: Calcula el porcentaje promedio de mejora en el tiempo de respuesta
 * de las acciones insertar y borrar al usar Árbol Binario en vez de Lista Enlazada Simple.
 */
public class BenchmarkHash {

    public static String compararRendimiento(List<Registro> registros, int metodoHash) {
        if (registros == null || registros.isEmpty()) {
            return "No hay registros cargados para realizar la comparación.";
        }

        // Claves de búsqueda por ID independientes para medir el borrado real
        List<Registro> clavesBorrar = new ArrayList<>(registros.size());
        for (int i = registros.size() - 1; i >= 0; i--) {
            clavesBorrar.add(new Registro(registros.get(i).getPlate()));
        }

        int repeticiones = 200;
        long totalInsertarLista = 0;
        long totalBorrarLista = 0;
        long totalInsertarArbol = 0;
        long totalBorrarArbol = 0;

        // Calentamiento previo de la JVM
        for (int w = 0; w < 80; w++) {
            TablaHashLista tl = new TablaHashLista(metodoHash);
            TablaHashArbol ta = new TablaHashArbol(metodoHash);
            for (Registro r : registros) {
                tl.insertar(r);
                ta.insertar(r);
            }
            for (Registro clave : clavesBorrar) {
                tl.eliminar(clave);
                ta.eliminar(clave);
            }
        }

        // Medición de tiempos con System.nanoTime()
        for (int rep = 0; rep < repeticiones; rep++) {
            TablaHashLista tablaLista = new TablaHashLista(metodoHash);
            long t0 = System.nanoTime();
            for (Registro r : registros) {
                tablaLista.insertar(r);
            }
            totalInsertarLista += (System.nanoTime() - t0);

            long t1 = System.nanoTime();
            for (Registro clave : clavesBorrar) {
                tablaLista.eliminar(clave);
            }
            totalBorrarLista += (System.nanoTime() - t1);

            TablaHashArbol tablaArbol = new TablaHashArbol(metodoHash);
            long t2 = System.nanoTime();
            for (Registro r : registros) {
                tablaArbol.insertar(r);
            }
            totalInsertarArbol += (System.nanoTime() - t2);

            long t3 = System.nanoTime();
            for (Registro clave : clavesBorrar) {
                tablaArbol.eliminar(clave);
            }
            totalBorrarArbol += (System.nanoTime() - t3);
        }

        double promInsertarLista = (double) totalInsertarLista / repeticiones;
        double promInsertarArbol = (double) totalInsertarArbol / repeticiones;
        double promBorrarLista = (double) totalBorrarLista / repeticiones;
        double promBorrarArbol = (double) totalBorrarArbol / repeticiones;

        double mejoraInsertar = ((promInsertarLista - promInsertarArbol) / promInsertarLista) * 100.0;
        double mejoraBorrar = ((promBorrarLista - promBorrarArbol) / promBorrarLista) * 100.0;
        double mejoraPromedio = (mejoraInsertar + mejoraBorrar) / 2.0;

        String nombreMetodo = (metodoHash == FuncionHash.METODO_MULTIPLICACION)
                ? "Multiplicación" : "División";

        return "Comparativa Tabla Hash: Lista Enlazada vs. Árbol Binario\n"
                + "--------------------------------------------------------\n"
                + "Registros evaluados: " + registros.size() + " | Función Hash: " + nombreMetodo + "\n\n"
                + String.format("1. Acción INSERTAR:\n   - Tiempo Lista Enlazada: %.3f ms\n   - Tiempo Árbol Binario:  %.3f ms\n   - Mejora porcentual:     %.2f %%\n\n",
                        promInsertarLista / 1_000_000.0, promInsertarArbol / 1_000_000.0, mejoraInsertar)
                + String.format("2. Acción BORRAR:\n   - Tiempo Lista Enlazada: %.3f ms\n   - Tiempo Árbol Binario:  %.3f ms\n   - Mejora porcentual:     %.2f %%\n\n",
                        promBorrarLista / 1_000_000.0, promBorrarArbol / 1_000_000.0, mejoraBorrar)
                + String.format("Mejora promedio global (Insertar y Borrar): %.2f %%", mejoraPromedio);
    }
}
