package ar.com.aeb.alquileres.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.expense.ExpenseNotFoundException;
import ar.com.aeb.alquileres.exception.expense.InvalidExpenseRequestException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.exception.rentalContract.RentalContractNotFoundException;
import ar.com.aeb.alquileres.exception.tenant.TenantNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getStatusCode().value());
        body.put("message", ex.getReason());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());

        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
            body.put("message", "The record already exists or the data entered (such as email, phone, or address) is duplicated and conflicts with another record.");
        } else {
            body.put("message", "Data integrity error in the database.");
        }

        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        body.put("message", errors);

        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTenantNotFound(TenantNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePropertyNotFound(PropertyNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(BuildingNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBuildingNotFound(BuildingNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(InvalidExpenseRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidExpenseRequest(InvalidExpenseRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleExpenseNotFound(ExpenseNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(RentalContractNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRentalContractNotFound(RentalContractNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getHttpStatus().value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllUncaughtException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
