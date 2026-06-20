package ar.com.aeb.alquileres.exception.tenant;

import org.springframework.http.HttpStatus;
import ar.com.aeb.alquileres.exception.CustomException;

public class TenantAlreadyInactiveException extends CustomException {

    public TenantAlreadyInactiveException(Long id) {
        super("Tenant is already inactive with id: " + id, HttpStatus.CONFLICT);
    }
}
