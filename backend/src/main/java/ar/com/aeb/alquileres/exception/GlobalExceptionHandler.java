package ar.com.aeb.alquileres.exception;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.expense.ExpenseNotFoundException;
import ar.com.aeb.alquileres.exception.expense.InvalidExpenseRequestException;
import ar.com.aeb.alquileres.exception.payment.InvalidPaymentAmountException;
import ar.com.aeb.alquileres.exception.payment.NoPendingContractException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.exception.rentalContract.RentalContractNotFoundException;
import ar.com.aeb.alquileres.exception.tenant.TenantNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        String userMsg = "Data integrity error in the database.";

        if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
            userMsg = "The record already exists or the data entered conflicts with another record.";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(HttpStatus.CONFLICT.value(), userMsg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTenantNotFound(TenantNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyNotFound(PropertyNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(BuildingNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBuildingNotFound(BuildingNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidExpenseRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidExpenseRequest(InvalidExpenseRequestException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpenseNotFound(ExpenseNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(RentalContractNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRentalContractNotFound(RentalContractNotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(NoPendingContractException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoPendingContract(NoPendingContractException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPaymentAmount(InvalidPaymentAmountException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
}
