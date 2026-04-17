package ar.com.aeb.alquileres.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("Phone number already exists in the system: " + phone);
    }
}
