package ar.com.aeb.alquileres.dto.rentalcontract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalContractRequest {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "The amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Due date cannot be null")
    private LocalDate dueDate;

    private org.springframework.web.multipart.MultipartFile contract;

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

    public org.springframework.web.multipart.MultipartFile getContract() {
        return contract;
    }

    public void setContract(org.springframework.web.multipart.MultipartFile contract) {
        this.contract = contract;
    }
}
