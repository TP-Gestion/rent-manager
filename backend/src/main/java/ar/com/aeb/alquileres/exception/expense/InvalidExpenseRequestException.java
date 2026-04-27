package ar.com.aeb.alquileres.exception.expense;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidExpenseRequestException extends CustomException {
    public InvalidExpenseRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
