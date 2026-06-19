package ar.com.aeb.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "RENTAL_CONTRACTS")
public class RentalContract extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotNull
    @Positive(message = "The amount must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalContractStatus status = RentalContractStatus.PENDING;

    @Column(name = "contract_path")
    private String contractPath;

    @ManyToOne
    private Tenant tenant;

    public RentalContract() {
    }

    public RentalContract(Property property, BigDecimal amount, LocalDate dueDate) {
        this.property = property;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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

    public RentalContractStatus getStatus() {
        return status;
    }

    public void setStatus(RentalContractStatus status) {
        this.status = status;
    }

    public String getContractPath() {
        return contractPath;
    }

    public void setContractPath(String contractPath) {
        this.contractPath = contractPath;
    }

    public enum RentalContractStatus {
        PENDING, PAID, OVERDUE
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
