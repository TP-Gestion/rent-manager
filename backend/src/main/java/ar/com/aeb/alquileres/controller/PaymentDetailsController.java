package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.paymentDetails.PaymentDetailsResponse;
import ar.com.aeb.alquileres.service.PaymentDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/payment-details")
@Tag(name = "Payment Details", description = "Operations for property payment details")
public class PaymentDetailsController {

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaymentDetailsResponse>> getPaymentDetails(@PathVariable Long propertyId) {
        PaymentDetailsResponse paymentDetails = paymentDetailsService.getPaymentDetails(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Success", paymentDetails));
    }
}
