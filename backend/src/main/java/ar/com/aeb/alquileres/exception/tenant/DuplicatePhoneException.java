package ar.com.aeb.alquileres.exception.tenant;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("Phone number already exists in the system: " + phone);
    }
}
