package ar.com.aeb.alquileres.dto.expense;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Payload para actualizar un gasto (PATCH). Por simplicidad solo se permite
 * cambiar el monto. Coincide con el type "UpdateExpenseRequest" del frontend.
 */
public class UpdateBuildingExpenseRequest {

    @NotNull(message = "The amount cannot be null")
    @Positive(message = "The amount must be greater than 0")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
