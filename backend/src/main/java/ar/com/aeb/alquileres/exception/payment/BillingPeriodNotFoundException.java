package ar.com.aeb.alquileres.exception.payment;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BillingPeriodNotFoundException extends CustomException {
    public BillingPeriodNotFoundException(Long propertyId, String period) {
        super("No billing found for property " + propertyId + " and period " + period, HttpStatus.NOT_FOUND);
    }
}
