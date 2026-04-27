package ar.com.aeb.alquileres.dto.paymentDetails;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentDetailsResponse {

    private BigDecimal rentAmount;
    private String rentStatus;
    private BigDecimal expensesAmount;
    private List<ExpenseResponse> expenses;
    private BigDecimal totalDue;
    private LocalDate nextDueDate;

    public PaymentDetailsResponse(BigDecimal rentAmount, String rentStatus, BigDecimal expensesAmount, List<ExpenseResponse> expenses) {
        this.rentAmount = rentAmount != null ? rentAmount : BigDecimal.ZERO;
        this.rentStatus = rentStatus;
        this.expensesAmount = expensesAmount != null ? expensesAmount : BigDecimal.ZERO;
        this.totalDue = this.rentAmount.add(this.expensesAmount);
        this.expenses = expenses;
    }

    // Getters and Setters
    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(String rentStatus) {
        this.rentStatus = rentStatus;
    }

    public BigDecimal getExpensesAmount() {
        return expensesAmount;
    }

    public void setExpensesAmount(BigDecimal expensesAmount) {
        this.expensesAmount = expensesAmount;
    }

    public List<ExpenseResponse> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseResponse> expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
