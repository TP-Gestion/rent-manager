package ar.com.aeb.alquileres.exception;

public class DuplicateActiveContractException extends RuntimeException {

    public DuplicateActiveContractException(String message) {
        super(message);
    }

    public DuplicateActiveContractException(String message, Throwable cause) {
        super(message, cause);
    }
}
