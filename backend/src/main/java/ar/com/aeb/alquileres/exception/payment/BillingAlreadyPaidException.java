package ar.com.aeb.alquileres.exception.payment;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BillingAlreadyPaidException extends CustomException {
    public BillingAlreadyPaidException(String period) {
        super("Billing for period " + period + " is already paid", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
