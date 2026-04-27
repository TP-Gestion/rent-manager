package ar.com.aeb.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "PROPERTY_EXPENSES")
public class PropertyExpense extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyExpenseStatus status = PropertyExpenseStatus.PENDING;

    @NotNull
    @Positive(message = "The amount must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    public PropertyExpense() {
    }

    public PropertyExpense(Expense expense, Property property, BigDecimal amount) {
        this.expense = expense;
        this.property = property;
        this.amount = amount;
    }

    // Getters y Setters

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public PropertyExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyExpenseStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public enum PropertyExpenseStatus {
        PENDING, PAID, OVERDUE
    }
}
