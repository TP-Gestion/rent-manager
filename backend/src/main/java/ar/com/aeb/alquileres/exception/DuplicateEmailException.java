package ar.com.aeb.alquileres.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already exists in the system: " + email);
    }
}
