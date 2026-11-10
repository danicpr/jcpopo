package co.edu.unicartagena.hash.model;

import java.util.Objects;

/**
 * Representa un registro del archivo MOCK_DATA.csv / PLATES.csv:
 * plate ; first_name ; last_name ; email; gender
 */
public class Registro {

    private final String plate;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;
    private final long k;

    public Registro(String plate, String firstName, String lastName, String email, String gender) {
        this.plate = plate != null ? plate.trim() : "";
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.gender = gender != null ? gender.trim() : "";
        this.k = FuncionHash.calcularLlaveRadix2(this.plate);
    }

    /**
     * Constructor auxiliar para búsquedas/eliminaciones por id (plate).
     */
    public Registro(String plate) {
        this(plate, "", "", "", "");
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

    public long getK() {
        return k;
    }

    public int getHash(FuncionHash.Metodo metodo) {
        return FuncionHash.calcularHash(k, metodo);
    }

    public String toCsvLine() {
        return plate + ";" + firstName + ";" + lastName + ";" + email + ";" + gender;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Registro other = (Registro) obj;
        return this.plate != null && this.plate.equalsIgnoreCase(other.plate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.plate);
    }

    @Override
    public String toString() {
        return plate + " | " + firstName + " " + lastName + " | " + email + " | " + gender + " (k=" + k + ")";
    }
}
