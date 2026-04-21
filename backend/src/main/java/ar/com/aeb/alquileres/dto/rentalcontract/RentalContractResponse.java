package ar.com.aeb.alquileres.dto.rentalcontract;

import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.model.RentalContract;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalContractResponse {

    private Long id;
    private TenantResponse tenant;
    private PropertyResponse property;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyRent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RentalContractResponse() {
    }

    public RentalContractResponse(RentalContract contract) {
        this.id = contract.getId();
        this.tenant = new TenantResponse(contract.getTenant());
        this.property = new PropertyResponse(contract.getProperty());
        this.startDate = contract.getStartDate();
        this.endDate = contract.getEndDate();
        this.monthlyRent = contract.getMonthlyRent();
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

    public TenantResponse getTenant() {
        return tenant;
    }

    public void setTenant(TenantResponse tenant) {
        this.tenant = tenant;
    }

    public PropertyResponse getProperty() {
        return property;
    }

    public void setProperty(PropertyResponse property) {
        this.property = property;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
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
