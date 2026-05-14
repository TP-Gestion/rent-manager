package ar.com.aeb.alquileres.exception.payment;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoPendingContractException extends CustomException {
    public NoPendingContractException(Long propertyId) {
        super("No pending or overdue rental contract found for property id: " + propertyId, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
