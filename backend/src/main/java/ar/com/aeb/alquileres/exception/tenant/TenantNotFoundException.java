package ar.com.aeb.alquileres.exception.tenant;

import org.springframework.http.HttpStatus;
import ar.com.aeb.alquileres.exception.CustomException;

public class TenantNotFoundException extends CustomException {

    public TenantNotFoundException(Long id) {
        super("Tenant not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
