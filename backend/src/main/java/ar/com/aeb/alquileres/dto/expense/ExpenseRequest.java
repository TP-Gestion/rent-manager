package ar.com.aeb.alquileres.dto.expense;

import ar.com.aeb.alquileres.model.Expense.ExpenseType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    @NotNull(message = "The expense type cannot be null")
    private ExpenseType type;

    @NotNull(message = "The amount cannot be null")
    @Positive(message = "The amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    private LocalDate dueDate;

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
