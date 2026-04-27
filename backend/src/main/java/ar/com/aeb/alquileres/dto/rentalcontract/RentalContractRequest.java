package ar.com.aeb.alquileres.dto.rentalcontract;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalContractRequest {

    private BigDecimal amount;
    private LocalDate dueDate;

    public RentalContractRequest() {
    }

    public RentalContractRequest(BigDecimal amount, LocalDate dueDate) {
        this.amount = amount;
        this.dueDate = dueDate;
    }

    // Getters and Setters
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
}
