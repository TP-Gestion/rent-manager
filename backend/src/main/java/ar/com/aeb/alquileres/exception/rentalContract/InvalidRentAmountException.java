package ar.com.aeb.alquileres.exception.rentalContract;

import org.springframework.http.HttpStatus;

import ar.com.aeb.alquileres.exception.CustomException;

public class InvalidRentAmountException extends CustomException {

    public InvalidRentAmountException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidRentAmountException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
