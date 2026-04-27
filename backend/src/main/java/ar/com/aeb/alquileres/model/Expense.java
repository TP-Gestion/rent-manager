package ar.com.aeb.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EXPENSES")
public class Expense extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyExpense> propertyExpenses = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", nullable = false)
    private ExpenseType type;

    @NotNull
    @Positive(message = "The amount must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    public Expense() {
    }

    // Getters y Setters

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<PropertyExpense> getPropertyExpenses() {
        return propertyExpenses;
    }

    public void setPropertyExpenses(List<PropertyExpense> propertyExpenses) {
        this.propertyExpenses = propertyExpenses;
    }

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

    public enum ExpenseType {
        MAINTENANCE, REPAIR, UTILITIES, TAXES, ADMINISTRATION
    }
}
