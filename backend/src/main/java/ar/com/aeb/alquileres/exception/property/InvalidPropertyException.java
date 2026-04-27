package ar.com.aeb.alquileres.exception.property;

import org.springframework.http.HttpStatus;

import ar.com.aeb.alquileres.exception.CustomException;

public class InvalidPropertyException extends CustomException {

    public InvalidPropertyException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidPropertyException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
