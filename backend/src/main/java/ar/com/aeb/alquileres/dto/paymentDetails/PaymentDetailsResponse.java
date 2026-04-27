package ar.com.aeb.alquileres.dto.paymentDetails;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentDetailsResponse {

    private BigDecimal rentAmount;
    private BigDecimal expensesAmount;
    private BigDecimal totalDue;
    private List<ExpenseResponse> expenses;
    private String paymentStatus;
    private LocalDate nextDueDate;

    public PaymentDetailsResponse(BigDecimal rentAmount, BigDecimal expensesAmount, List<ExpenseResponse> expenses) {
        this.rentAmount = rentAmount != null ? rentAmount : BigDecimal.ZERO;
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

    public BigDecimal getExpensesAmount() {
        return expensesAmount;
    }

    public void setExpensesAmount(BigDecimal expensesAmount) {
        this.expensesAmount = expensesAmount;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public List<ExpenseResponse> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseResponse> expenses) {
        this.expenses = expenses;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
