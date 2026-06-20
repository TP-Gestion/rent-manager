package ar.com.aeb.alquileres.exception.tenant;

import org.springframework.http.HttpStatus;
import ar.com.aeb.alquileres.exception.CustomException;

public class DuplicatePhoneException extends CustomException {

    public DuplicatePhoneException(String phone) {
        super("Phone number already exists in the system: " + phone, HttpStatus.CONFLICT);
    }
}
