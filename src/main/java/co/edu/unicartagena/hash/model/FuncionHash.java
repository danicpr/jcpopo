package co.edu.unicartagena.hash.model;

/**
 * Funciones Hash y cálculo de la llave k en notación radix-2 (reducción ASCII).
 */
public class FuncionHash {

    public static final int M = 256;
    public static final double A = (Math.sqrt(5.0) - 1.0) / 2.0;

    public static final int METODO_MULTIPLICACION = 1;
    public static final int METODO_DIVISION = 2;

    /**
     * Calcula la llave k en notación radix-2:
     * Ejemplo "05-9165610" -> (48*2^9) + (53*2^8) + ... + (48*2^0) = 50770
     */
    public static long calcularLlaveRadix2(String plate) {
        if (plate == null) {
            return 0;
        }
        String texto = plate.trim();
        int n = texto.length();
        long k = 0;
        for (int i = 0; i < n; i++) {
            int ascii = (int) texto.charAt(i);
            int exponente = (n - 1) - i;
            k += ascii * (long) Math.pow(2, exponente);
        }
        return k;
    }

    /**
     * 1.- Método de la multiplicación: h(k) = floor(m * (k * A mod 1))
     */
    public static int hashMultiplicacion(long k) {
        double producto = k * A;
        double parteFraccionaria = producto - Math.floor(producto);
        return (int) Math.floor(M * parteFraccionaria);
    }

    /**
     * 2.- Método de la división: h(k) = k mod m
     */
    public static int hashDivision(long k) {
        return (int) Math.abs(k % M);
    }

    public static int calcularHash(long k, int metodo) {
        if (metodo == METODO_MULTIPLICACION) {
            return hashMultiplicacion(k);
        } else {
            return hashDivision(k);
        }
    }
}
