package ar.com.aeb.alquileres.dto.expense;

import ar.com.aeb.alquileres.model.Expense;
import ar.com.aeb.alquileres.model.Expense.ExpenseType;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.PropertyExpense.PropertyExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseResponse {
    private Long instanceId;
    private Long expenseId;
    private Long propertyId;
    private ExpenseType type;
    private BigDecimal amount;
    private String description;
    private PropertyExpenseStatus status;
    private LocalDateTime createdAt;
    private LocalDate dueDate;

    public ExpenseResponse(PropertyExpense propertyExpense) {
        Expense expense = propertyExpense.getExpense();
        this.instanceId = propertyExpense.getId();
        this.expenseId = expense.getId();
        this.propertyId = propertyExpense.getProperty().getId();
        this.type = expense.getType();
        this.amount = propertyExpense.getAmount(); // Monto de la instancia
        this.description = expense.getDescription();
        this.status = propertyExpense.getStatus();
        this.createdAt = propertyExpense.getCreatedAt();
        this.dueDate = expense.getDueDate();
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(final Long propertyId) {
        this.propertyId = propertyId;
    }

    public ExpenseType getType() {
        return type;
    }

    public void setType(final ExpenseType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public PropertyExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(final PropertyExpenseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
