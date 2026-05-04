package ar.com.aeb.alquileres.exception.payment;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends CustomException {
    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
