package ar.com.aeb.alquileres.exception;

import org.springframework.http.HttpStatus;

public class TenantNotFoundException extends CustomException {

    public TenantNotFoundException(Long id) {
        super("Tenant not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
