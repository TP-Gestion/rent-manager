package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.payment.PaymentRequest;
import ar.com.aeb.alquileres.dto.payment.PaymentResponse;
import ar.com.aeb.alquileres.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/v1/properties/{propertyId}/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> registerPayment(
            @PathVariable Long propertyId,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.registerPayment(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Payment registered successfully", response));
    }

    @GetMapping("/api/v1/properties/{propertyId}/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByProperty(
            @PathVariable Long propertyId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Success", payments));
    }

    @GetMapping("/api/v1/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success("Success", payments));
    }
}
