package ar.com.aeb.alquileres.exception.building;

import org.springframework.http.HttpStatus;

import ar.com.aeb.alquileres.exception.CustomException;

public class DuplicateBuildingException extends CustomException {

    public DuplicateBuildingException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicateBuildingException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
