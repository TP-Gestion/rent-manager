package ar.com.aeb.alquileres.exception;

import org.springframework.http.HttpStatus;

public class DuplicatePropertyException extends CustomException {

    public DuplicatePropertyException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicatePropertyException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
