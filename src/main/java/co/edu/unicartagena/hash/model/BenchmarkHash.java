package co.edu.unicartagena.hash.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Evalúa experimentalmente y analíticamente la mejora porcentual en el tiempo de respuesta
 * de las operaciones Insertar, Borrar y Buscar al reemplazar la Lista Enlazada Simple por un
 * Árbol Binario de Búsqueda en cada slot de la Tabla Hash.
 */
public class BenchmarkHash {

    public static class ResultadoMetodo {
        public final FuncionHash.Metodo metodo;
        public final int cantidadRegistros;
        public final int repeticiones;
        public final double tiempoInsertarListaNs;
        public final double tiempoInsertarArbolNs;
        public final double mejoraInsertarPorcentaje;
        public final double tiempoBorrarListaNs;
        public final double tiempoBorrarArbolNs;
        public final double mejoraBorrarPorcentaje;
        public final double tiempoBuscarListaNs;
        public final double tiempoBuscarArbolNs;
        public final double mejoraBuscarPorcentaje;
        public final double pasosPromedioInsertarLista;
        public final double pasosPromedioInsertarArbol;
        public final double mejoraPasosInsertarPorcentaje;
        public final double pasosPromedioBorrarLista;
        public final double pasosPromedioBorrarArbol;
        public final double mejoraPasosBorrarPorcentaje;

        public ResultadoMetodo(
                FuncionHash.Metodo metodo,
                int cantidadRegistros,
                int repeticiones,
                double tiempoInsertarListaNs,
                double tiempoInsertarArbolNs,
                double tiempoBorrarListaNs,
                double tiempoBorrarArbolNs,
                double tiempoBuscarListaNs,
                double tiempoBuscarArbolNs,
                double pasosPromedioInsertarLista,
                double pasosPromedioInsertarArbol,
                double pasosPromedioBorrarLista,
                double pasosPromedioBorrarArbol) {
            this.metodo = metodo;
            this.cantidadRegistros = cantidadRegistros;
            this.repeticiones = repeticiones;
            this.tiempoInsertarListaNs = tiempoInsertarListaNs;
            this.tiempoInsertarArbolNs = tiempoInsertarArbolNs;
            this.mejoraInsertarPorcentaje = ((tiempoInsertarListaNs - tiempoInsertarArbolNs) / tiempoInsertarListaNs) * 100.0;
            this.tiempoBorrarListaNs = tiempoBorrarListaNs;
            this.tiempoBorrarArbolNs = tiempoBorrarArbolNs;
            this.mejoraBorrarPorcentaje = ((tiempoBorrarListaNs - tiempoBorrarArbolNs) / tiempoBorrarListaNs) * 100.0;
            this.tiempoBuscarListaNs = tiempoBuscarListaNs;
            this.tiempoBuscarArbolNs = tiempoBuscarArbolNs;
            this.mejoraBuscarPorcentaje = ((tiempoBuscarListaNs - tiempoBuscarArbolNs) / tiempoBuscarListaNs) * 100.0;
            this.pasosPromedioInsertarLista = pasosPromedioInsertarLista;
            this.pasosPromedioInsertarArbol = pasosPromedioInsertarArbol;
            this.mejoraPasosInsertarPorcentaje = ((pasosPromedioInsertarLista - pasosPromedioInsertarArbol) / pasosPromedioInsertarLista) * 100.0;
            this.pasosPromedioBorrarLista = pasosPromedioBorrarLista;
            this.pasosPromedioBorrarArbol = pasosPromedioBorrarArbol;
            this.mejoraPasosBorrarPorcentaje = ((pasosPromedioBorrarLista - pasosPromedioBorrarArbol) / pasosPromedioBorrarLista) * 100.0;
        }
    }

    /**
     * Ejecuta el benchmark sobre la lista de registros proporcionada (por ejemplo los 1000 registros de MOCK_DATA.csv)
     * para el método hash indicado, realizando calentamiento de JVM y promediando sobre múltiples iteraciones.
     */
    public static ResultadoMetodo ejecutarBenchmark(List<Registro> base, FuncionHash.Metodo metodo, int iteraciones) {
        List<Registro> datos = (base != null && !base.isEmpty()) ? base : generarRegistrosSinteticos(1000);
        int n = datos.size();
        String[] placas = new String[n];
        for (int i = 0; i < n; i++) {
            placas[i] = datos.get(i).getPlate();
        }

        // Fase de calentamiento (JIT Warmup)
        for (int w = 0; w < 40; w++) {
            TablaHashLista tl = new TablaHashLista(metodo);
            TablaHashArbol ta = new TablaHashArbol(metodo);
            for (int i = 0; i < n; i++) {
                tl.insertar(datos.get(i));
                ta.insertar(datos.get(i));
            }
            for (int i = 0; i < n; i++) {
                tl.buscar(placas[i]);
                ta.buscar(placas[i]);
            }
            for (int i = n - 1; i >= 0; i--) {
                tl.eliminar(placas[i]);
                ta.eliminar(placas[i]);
            }
        }

        long acumInsLista = 0L;
        long acumInsArbol = 0L;
        long acumBusLista = 0L;
        long acumBusArbol = 0L;
        long acumBorLista = 0L;
        long acumBorArbol = 0L;

        for (int it = 0; it < iteraciones; it++) {
            TablaHashLista tl = new TablaHashLista(metodo);
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                tl.insertar(datos.get(i));
            }
            long t1 = System.nanoTime();
            acumInsLista += (t1 - t0);

            TablaHashArbol ta = new TablaHashArbol(metodo);
            long t2 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                ta.insertar(datos.get(i));
            }
            long t3 = System.nanoTime();
            acumInsArbol += (t3 - t2);

            long t4 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                tl.buscar(placas[i]);
            }
            long t5 = System.nanoTime();
            acumBusLista += (t5 - t4);

            long t6 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                ta.buscar(placas[i]);
            }
            long t7 = System.nanoTime();
            acumBusArbol += (t7 - t6);

            // Borrado en orden inverso al de inserción (recorre cada cadena/subárbol sin sesgo FIFO)
            long t8 = System.nanoTime();
            for (int i = n - 1; i >= 0; i--) {
                tl.eliminar(placas[i]);
            }
            long t9 = System.nanoTime();
            acumBorLista += (t9 - t8);

            long t10 = System.nanoTime();
            for (int i = n - 1; i >= 0; i--) {
                ta.eliminar(placas[i]);
            }
            long t11 = System.nanoTime();
            acumBorArbol += (t11 - t10);
        }

        double promInsListaNs = (double) acumInsLista / (iteraciones * (double) n);
        double promInsArbolNs = (double) acumInsArbol / (iteraciones * (double) n);
        double promBorListaNs = (double) acumBorLista / (iteraciones * (double) n);
        double promBorArbolNs = (double) acumBorArbol / (iteraciones * (double) n);
        double promBusListaNs = (double) acumBusLista / (iteraciones * (double) n);
        double promBusArbolNs = (double) acumBusArbol / (iteraciones * (double) n);

        // Conteo exacto de comparaciones/nodos visitados sobre el dataset
        double[] conteos = calcularComparacionesExactas(datos, metodo);

        return new ResultadoMetodo(
                metodo,
                n,
                iteraciones,
                promInsListaNs,
                promInsArbolNs,
                promBorListaNs,
                promBorArbolNs,
                promBusListaNs,
                promBusArbolNs,
                conteos[0],
                conteos[1],
                conteos[2],
                conteos[3]
        );
    }

    /**
     * Calcula el número exacto promedio de nodos visitados al insertar y borrar todos los elementos
     * del dataset en cada slot para Lista Enlazada Simple vs. Árbol Binario de Búsqueda.
     */
    private static double[] calcularComparacionesExactas(List<Registro> datos, FuncionHash.Metodo metodo) {
        int n = datos.size();
        int[] tamSlot = new int[FuncionHash.M];
        long pasosInsLista = 0L;
        long pasosBorLista = 0L;

        TablaHashArbol ta = new TablaHashArbol(metodo);
        long pasosInsArbol = 0L;
        for (Registro r : datos) {
            int slot = FuncionHash.calcularHash(r.getK(), metodo);
            int c = tamSlot[slot];
            pasosInsLista += (c + 1);
            pasosBorLista += (c + 1);
            tamSlot[slot] = c + 1;

            int prof = medirPasosInsercionABB(ta.getSlot(slot).getRaiz(), r.getK(), r.getPlate());
            pasosInsArbol += prof;
            ta.insertar(r);
        }
        long pasosBorArbol = pasosInsArbol;
        return new double[]{
            (double) pasosInsLista / n,
            (double) pasosInsArbol / n,
            (double) pasosBorLista / n,
            (double) pasosBorArbol / n
        };
    }

    private static int medirPasosInsercionABB(co.edu.unicartagena.binarytree.Node raiz, long k, String idClave) {
        int pasos = 1;
        co.edu.unicartagena.binarytree.Node actual = raiz;
        while (actual != null) {
            pasos++;
            if (k < actual.datos) {
                actual = actual.izquierdo;
            } else if (k > actual.datos) {
                actual = actual.derecho;
            } else {
                int cmp = idClave.compareTo(actual.idClave);
                if (cmp == 0) {
                    break;
                } else if (cmp < 0) {
                    actual = actual.izquierdo;
                } else {
                    actual = actual.derecho;
                }
            }
        }
        return pasos;
    }

    public static List<Registro> generarRegistrosSinteticos(int cantidad) {
        List<Registro> lista = new ArrayList<>(cantidad);
        Random rnd = new Random(20261030L);
        lista.add(new Registro("05-9165610", "Letitia", "Maymand", "tres@x.com", "Female"));
        for (int i = 1; i < cantidad; i++) {
            String plate = String.format("%02d-%07d", rnd.nextInt(100), 1000000 + rnd.nextInt(9000000));
            lista.add(new Registro(plate, "User" + i, "Last" + i, "user" + i + "@x.com", (i % 2 == 0) ? "Female" : "Male"));
        }
        return lista;
    }
}
