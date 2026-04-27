package ar.com.aeb.alquileres.exception.expense;

import ar.com.aeb.alquileres.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ExpenseNotFoundException extends CustomException {
    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
