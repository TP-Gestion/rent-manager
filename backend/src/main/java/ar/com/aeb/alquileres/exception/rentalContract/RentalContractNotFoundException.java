package ar.com.aeb.alquileres.exception.rentalContract;

import org.springframework.http.HttpStatus;

import ar.com.aeb.alquileres.exception.CustomException;

public class RentalContractNotFoundException extends CustomException {
    public RentalContractNotFoundException(Long id) {
        super("Rental contract not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
