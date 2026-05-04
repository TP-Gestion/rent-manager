package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.billing.BillingResponse;
import ar.com.aeb.alquileres.dto.payment.PaymentRequest;
import ar.com.aeb.alquileres.dto.payment.PaymentResponse;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.repository.BillingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> registerPayment(
            @PathVariable Long propertyId,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.registerPayment(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Payment registered successfully", response));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByProperty(
            @PathVariable Long propertyId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Success", payments));
    }

    @GetMapping("/billings")
    public ResponseEntity<ApiResponse<List<BillingResponse>>> getBillingsByProperty(
            @PathVariable Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new PropertyNotFoundException(propertyId);
        }
        List<BillingResponse> billings = billingRepository.findByPropertyId(propertyId).stream()
                .map(BillingResponse::new)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Success", billings));
    }
}
