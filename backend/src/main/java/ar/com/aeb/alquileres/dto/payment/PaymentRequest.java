package ar.com.aeb.alquileres.dto.payment;

import ar.com.aeb.alquileres.model.Payment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentRequest {

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "Debe ingresar un monto válido")
    private BigDecimal amount;

    @NotNull(message = "Debe seleccionar un medio de pago válido")
    private Payment.PaymentMethod paymentMethod;

    @NotNull(message = "La fecha de pago es obligatoria")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate paymentDate;

    private String reference;

    private String notes;

    @NotNull(message = "Debe seleccionar al menos un período a pagar")
    @NotEmpty(message = "Debe seleccionar al menos un período a pagar")
    private List<String> selectedPeriods;

    private org.springframework.web.multipart.MultipartFile receipt;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Payment.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Payment.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
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

    public List<String> getSelectedPeriods() {
        return selectedPeriods;
    }

    public void setSelectedPeriods(List<String> selectedPeriods) {
        this.selectedPeriods = selectedPeriods;
    }

    public org.springframework.web.multipart.MultipartFile getReceipt() {
        return receipt;
    }

    public void setReceipt(org.springframework.web.multipart.MultipartFile receipt) {
        this.receipt = receipt;
    }
}
