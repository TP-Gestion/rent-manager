package ar.com.aeb.alquileres.exception.property;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PropertyNotFoundException extends CustomException {
    public PropertyNotFoundException(Long id) {
        super("Property not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
