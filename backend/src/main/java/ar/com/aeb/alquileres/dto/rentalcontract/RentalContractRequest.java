package ar.com.aeb.alquileres.dto.rentalcontract;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalContractRequest {

    private Long tenantId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;

    public RentalContractRequest() {
    }

    public RentalContractRequest(Long tenantId, Long propertyId, LocalDate startDate,
                                  LocalDate endDate, BigDecimal monthlyRent) {
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
    }

    // Getters and Setters
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
}
