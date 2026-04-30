package ar.com.aeb.alquileres.dto.rentalcontract;

import ar.com.aeb.alquileres.model.RentalContract;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalContractResponse {

    private Long id;
    private Long propertyId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RentalContractResponse() {
    }

    public RentalContractResponse(RentalContract contract) {
        this.id = contract.getId();
        this.propertyId = contract.getProperty().getId();
        this.amount = contract.getAmount();
        this.dueDate = contract.getDueDate();
        this.status = contract.getStatus().toString();
        this.createdAt = contract.getCreatedAt();
        this.updatedAt = contract.getUpdatedAt();
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
