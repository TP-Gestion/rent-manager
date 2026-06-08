package ar.com.aeb.alquileres.dto.expense;

import ar.com.aeb.alquileres.model.Expense;
import ar.com.aeb.alquileres.model.Expense.ExpenseFrequency;
import ar.com.aeb.alquileres.model.Expense.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta a nivel edificio para un gasto. Coincide con el type "ExpenseItem"
 * del frontend (un item por gasto, no por propiedad).
 */
public class BuildingExpenseResponse {

    private Long id;
    private String buildingName;
    private ExpenseType type;
    private String category;
    private String concept;
    private ExpenseFrequency frequency;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public BuildingExpenseResponse(Expense expense) {
        this.id = expense.getId();
        this.buildingName = expense.getBuilding() != null ? expense.getBuilding().getName() : null;
        this.type = expense.getType();
        this.category = expense.getCategory();
        this.concept = expense.getDescription();
        this.frequency = resolveFrequency(expense);
        this.amount = expense.getAmount();
        this.createdAt = expense.getCreatedAt();
    }

    /**
     * Si la fila no tiene frequency (columna nueva, null en datos viejos), la deriva
     * del type: ORDINARIA -> MENSUAL, EXTRAORDINARIA -> UNICA.
     */
    private static ExpenseFrequency resolveFrequency(Expense expense) {
        if (expense.getFrequency() != null) {
            return expense.getFrequency();
        }
        if (expense.getType() == null) {
            return null;
        }
        return expense.getType() == ExpenseType.ORDINARIA
                ? ExpenseFrequency.MENSUAL
                : ExpenseFrequency.UNICA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

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

    public ExpenseFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(ExpenseFrequency frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
