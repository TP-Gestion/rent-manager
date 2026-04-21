package ar.com.aeb.alquileres.exception;

import org.springframework.http.HttpStatus;

public class DuplicateBuildingException extends CustomException {

    public DuplicateBuildingException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicateBuildingException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
