package ar.com.aeb.alquileres.dto.billing;

import ar.com.aeb.alquileres.model.Billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BillingResponse {

    private Long id;
    private String period;
    private String status;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paymentDate;

    public BillingResponse() {
    }

    public BillingResponse(Billing billing) {
        this.id = billing.getId();
        this.period = billing.getPeriod();
        this.status = billing.getStatus().name();
        this.amount = billing.getTotalAmount();
        this.dueDate = billing.getDueDate();
        this.paymentDate = billing.getPayment() != null ? billing.getPayment().getPaymentDate() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
