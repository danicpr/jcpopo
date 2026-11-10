package co.edu.unicartagena.hash.model;

/**
 * Implementación de las funciones Hash y cálculo de llave radix-2 (reducción ASCII)
 * según las especificaciones del Taller de Tablas Hash.
 */
public final class FuncionHash {

    public static final int M = 256;
    public static final double A = (Math.sqrt(5.0) - 1.0) / 2.0;

    public enum Metodo {
        MULTIPLICACION("Método 1: Multiplicación  h(k) = ⌊m(kA mod 1)⌋"),
        DIVISION("Método 2: División  h(k) = k mod m");

        private final String etiqueta;

        Metodo(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private FuncionHash() {
    }

    /**
     * Calcula la llave k en notación radix-2 a partir del código ASCII de cada carácter del id (plate).
     * Ejemplo para "05-9165610":
     * (48*2^9)+(53*2^8)+(45*2^7)+(57*2^6)+(49*2^5)+(54*2^4)+(53*2^3)+(54*2^2)+(49*2^1)+(48*2^0) = 50770
     */
    public static long calcularLlaveRadix2(String plate) {
        if (plate == null) {
            return 0L;
        }
        String limpio = plate.trim();
        int n = limpio.length();
        long k = 0L;
        for (int i = 0; i < n; i++) {
            int ascii = (int) limpio.charAt(i);
            int exp = n - 1 - i;
            k += (long) ascii * (1L << exp);
        }
        return k;
    }

    /**
     * Genera la expresión paso a paso de la reducción ASCII en radix-2.
     */
    public static String explicarReduccionAscii(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return "0";
        }
        String limpio = plate.trim();
        int n = limpio.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append("+");
            }
            int ascii = (int) limpio.charAt(i);
            int exp = n - 1 - i;
            sb.append("(").append(ascii).append(".2^").append(exp).append(")");
        }
        return sb.toString();
    }

    /**
     * 1.- Método de la Multiplicación:
     * h(k) = floor( m * ((k * A) mod 1) ), donde m = 256 y A = (sqrt(5) - 1) / 2
     */
    public static int hashMultiplicacion(long k) {
        double prod = k * A;
        double parteFraccionaria = prod - Math.floor(prod);
        int slot = (int) Math.floor(M * parteFraccionaria);
        if (slot < 0) {
            slot = 0;
        } else if (slot >= M) {
            slot = M - 1;
        }
        return slot;
    }

    /**
     * 2.- Método de la División:
     * h(k) = k mod m, donde m = 256
     */
    public static int hashDivision(long k) {
        int mod = (int) (k % M);
        return mod < 0 ? mod + M : mod;
    }

    public static int calcularHash(long k, Metodo metodo) {
        if (metodo == Metodo.MULTIPLICACION) {
            return hashMultiplicacion(k);
        }
        return hashDivision(k);
    }
}
