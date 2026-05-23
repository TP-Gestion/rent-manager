package ar.com.aeb.alquileres.dto.payment;

import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentResponse {

    private Long id;
    private Long propertyId;
    private LocalDate date;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    private String notes;
    private boolean hasReceipt;
    private List<String> periods;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.propertyId = payment.getProperty().getId();
        this.date = payment.getPaymentDate();
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod().toString();
        this.reference = payment.getReference();
        this.notes = payment.getNotes();
        this.hasReceipt = payment.getReceiptPath() != null;
        this.periods = payment.getBillings().stream().map(Billing::getPeriod).toList();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isHasReceipt() {
        return hasReceipt;
    }

    public void setHasReceipt(boolean hasReceipt) {
        this.hasReceipt = hasReceipt;
    }

    public List<String> getPeriods() {
        return periods;
    }

    public void setPeriods(List<String> periods) {
        this.periods = periods;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
