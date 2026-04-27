package ar.com.aeb.alquileres.exception.tenant;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already exists in the system: " + email);
    }
}
