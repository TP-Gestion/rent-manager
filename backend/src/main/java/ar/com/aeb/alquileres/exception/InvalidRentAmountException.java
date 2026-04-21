package ar.com.aeb.alquileres.exception;

public class InvalidRentAmountException extends RuntimeException {

    public InvalidRentAmountException(String message) {
        super(message);
    }

    public InvalidRentAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
