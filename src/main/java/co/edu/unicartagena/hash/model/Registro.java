package co.edu.unicartagena.hash.model;

/**
 * Representa un registro del archivo MOCK_DATA.csv / PLATES.csv.
 * Soporta tanto 4 columnas (plate;first_name;last_name;gender) como
 * 5 columnas (plate;first_name;last_name;email;gender).
 */
public class Registro {

    private String plate;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private long llaveK;

    public Registro(String plate, String firstName, String lastName, String email, String gender) {
        this.plate = plate != null ? plate.trim() : "";
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.gender = gender != null ? gender.trim() : "";
        this.llaveK = FuncionHash.calcularLlaveRadix2(this.plate);
    }

    public Registro(String plate) {
        this.plate = plate != null ? plate.trim() : "";
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.gender = "";
        this.llaveK = FuncionHash.calcularLlaveRadix2(this.plate);
    }

    public static Registro desdeCsv(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length < 4) {
            return null;
        }
        if (partes[0].trim().equalsIgnoreCase("plate")) {
            return null; // Ignorar fila de encabezado si existe
        }
        if (partes.length == 4) {
            return new Registro(partes[0], partes[1], partes[2], "", partes[3]);
        }
        return new Registro(partes[0], partes[1], partes[2], partes[3], partes[4]);
    }

    public String toCsv() {
        if (email == null || email.isEmpty()) {
            return plate + ";" + firstName + ";" + lastName + ";" + gender;
        }
        return plate + ";" + firstName + ";" + lastName + ";" + email + ";" + gender;
    }

    public String getPlate() {
        return plate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public long getLlaveK() {
        return llaveK;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Registro)) {
            return false;
        }
        Registro otro = (Registro) obj;
        return this.plate.equalsIgnoreCase(otro.plate);
    }

    @Override
    public int hashCode() {
        return plate.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return plate + " - " + firstName + " " + lastName;
    }
}
