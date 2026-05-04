package ar.com.aeb.alquileres.dto.payment;

import ar.com.aeb.alquileres.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long propertyId;
    private Long rentalContractId;
    private LocalDate paymentDate;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String notes;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.propertyId = payment.getProperty().getId();
        this.rentalContractId = payment.getRentalContract().getId();
        this.paymentDate = payment.getPaymentDate();
        this.totalAmount = payment.getTotalAmount();
        this.paymentMethod = payment.getPaymentMethod().toString();
        this.notes = payment.getNotes();
        this.createdAt = payment.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getRentalContractId() {
        return rentalContractId;
    }

    public void setRentalContractId(Long rentalContractId) {
        this.rentalContractId = rentalContractId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
