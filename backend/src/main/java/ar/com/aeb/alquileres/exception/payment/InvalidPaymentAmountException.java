package ar.com.aeb.alquileres.exception.payment;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidPaymentAmountException extends CustomException {
    public InvalidPaymentAmountException(java.math.BigDecimal expected, java.math.BigDecimal received) {
        super("Payment amount " + received + " does not match the contract amount " + expected, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
