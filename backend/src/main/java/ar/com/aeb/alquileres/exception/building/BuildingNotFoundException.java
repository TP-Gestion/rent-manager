package ar.com.aeb.alquileres.exception.building;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BuildingNotFoundException extends CustomException {
    public BuildingNotFoundException(Long id) {
        super("Building not found with id: " + id, HttpStatus.NOT_FOUND);
    }

}
