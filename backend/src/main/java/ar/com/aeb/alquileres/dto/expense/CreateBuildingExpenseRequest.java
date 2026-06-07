package ar.com.aeb.alquileres.dto.expense;

import ar.com.aeb.alquileres.model.Expense.ExpenseType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Payload que envía el front al crear un gasto de un edificio (POST).
 * Coincide con el type "CreateExpenseRequest" del frontend.
 * La frecuencia no se envía: se deriva del type en el backend.
 */
public class CreateBuildingExpenseRequest {

    @NotNull(message = "The expense type cannot be null")
    private ExpenseType type;

    private String category;

    private String concept;

    @NotNull(message = "The amount cannot be null")
    @Positive(message = "The amount must be greater than 0")
    private BigDecimal amount;

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
