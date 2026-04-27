package ar.com.aeb.alquileres.exception.property;

import org.springframework.http.HttpStatus;

import ar.com.aeb.alquileres.exception.CustomException;

public class DuplicatePropertyException extends CustomException {

    public DuplicatePropertyException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicatePropertyException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
