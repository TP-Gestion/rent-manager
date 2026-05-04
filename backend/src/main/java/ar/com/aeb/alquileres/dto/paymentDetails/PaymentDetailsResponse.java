package ar.com.aeb.alquileres.dto.paymentDetails;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDetailsResponse {

    private RentalContractResponse rentalContract;
    private List<ExpenseResponse> expenses;
    private BigDecimal totalDue;
    private String paymentStatus;
    private LocalDate earliestDueDate;

    public PaymentDetailsResponse(RentalContractResponse rentalContract, List<ExpenseResponse> expenses) {
        this.rentalContract = rentalContract;
        this.expenses = expenses;
        this.totalDue = calculateTotalDue(rentalContract, expenses);
    }

    private BigDecimal calculateTotalDue(RentalContractResponse rentalContract, List<ExpenseResponse> expenses) {
        BigDecimal total = BigDecimal.ZERO;

        if (rentalContract != null && rentalContract.getAmount() != null) {
            total = total.add(rentalContract.getAmount());
        }

        if (expenses != null) {
            for (ExpenseResponse expense : expenses) {
                if (expense.getAmount() != null) {
                    total = total.add(expense.getAmount());
                }
            }
        }

        return total;
    }

    // Getters and Setters
    public RentalContractResponse getRentalContract() {
        return rentalContract;
    }

    public void setRentalContract(RentalContractResponse rentalContract) {
        this.rentalContract = rentalContract;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getEarliestDueDate() {
        return earliestDueDate;
    }

    public void setEarliestDueDate(LocalDate earliestDueDate) {
        this.earliestDueDate = earliestDueDate;
    }
}
