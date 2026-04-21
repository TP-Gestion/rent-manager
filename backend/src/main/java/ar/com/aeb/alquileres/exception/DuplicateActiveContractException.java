package ar.com.aeb.alquileres.exception;

import org.springframework.http.HttpStatus;

public class DuplicateActiveContractException extends CustomException {

    public DuplicateActiveContractException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicateActiveContractException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
