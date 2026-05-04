package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.dto.paymentDetails.PaymentDetailsResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentDetailsService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));
        return buildPaymentDetails(property);
    }

    private PaymentDetailsResponse buildPaymentDetails(Property property) {
        // Get all non-paid expenses
        List<PropertyExpense> pendingExpenses = property.getPropertyExpenses().stream().filter(pe -> pe.getStatus() != PropertyExpense.PropertyExpenseStatus.PAID).collect(Collectors.toList());

        List<ExpenseResponse> expenseDetails = pendingExpenses.stream().map(ExpenseResponse::new).collect(Collectors.toList());

        // Get non-paid rental contracts
        List<RentalContract> contracts = rentalContractRepository.findByPropertyId(property.getId()).stream().filter(c -> c.getStatus() != RentalContract.RentalContractStatus.PAID).collect(Collectors.toList());

        RentalContractResponse activeContractResponse = null;
        if (!contracts.isEmpty()) {
            activeContractResponse = new RentalContractResponse(contracts.get(0));
        }

        PaymentDetailsResponse response = new PaymentDetailsResponse(activeContractResponse, expenseDetails);

        // Calculate Status and Earliest Due Date (Standardizing with Summary)
        boolean hasOverdue = false;
        boolean hasPending = false;
        LocalDate earliestDate = null;

        for (RentalContract contract : contracts) {
            hasPending = true;
            if (contract.getStatus() == RentalContract.RentalContractStatus.OVERDUE) {
                hasOverdue = true;
            }
            if (earliestDate == null || contract.getDueDate().isBefore(earliestDate)) {
                earliestDate = contract.getDueDate();
            }
        }

        for (PropertyExpense pe : pendingExpenses) {
            hasPending = true;
            if (pe.getStatus() == PropertyExpense.PropertyExpenseStatus.OVERDUE) {
                hasOverdue = true;
            }
            // If expenses had a due date, we would check it here
        }

        if (hasOverdue) {
            response.setPaymentStatus("OVERDUE");
        } else if (hasPending) {
            response.setPaymentStatus("PENDING");
        } else {
            response.setPaymentStatus("PAID");
        }

        response.setEarliestDueDate(earliestDate);

        return response;
    }
}
