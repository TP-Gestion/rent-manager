package ar.com.aeb.alquileres.exception.tenant;

import org.springframework.http.HttpStatus;
import ar.com.aeb.alquileres.exception.CustomException;

public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException(String email) {
        super("Email already exists in the system: " + email, HttpStatus.CONFLICT);
    }
}
