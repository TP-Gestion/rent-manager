package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.dto.paymentDetails.PaymentDetailsResponse;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<PropertyExpense> pendingExpenses = property.getPropertyExpenses().stream().filter(pe -> pe.getStatus() == PropertyExpense.PropertyExpenseStatus.PENDING).toList();

        List<ExpenseResponse> expenseDetails = property.getPropertyExpenses().stream().map(ExpenseResponse::new).collect(Collectors.toList());

        BigDecimal totalExpenses = property.getPropertyExpenses().stream().map(PropertyExpense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get active rental contract with its status
        BigDecimal rentAmount = rentalContractRepository.findByPropertyIdAndStatus(property.getId(), RentalContract.RentalContractStatus.PENDING).stream().findFirst().map(c -> c.getAmount()).orElse(BigDecimal.ZERO);

        String rentStatus = "PENDING";

        // Find next due date from pending expenses
        LocalDate nextDueDate = pendingExpenses.stream().map(pe -> pe.getExpense().getDueDate()).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);

        PaymentDetailsResponse response = new PaymentDetailsResponse(rentAmount, rentStatus, totalExpenses, expenseDetails);
        response.setNextDueDate(nextDueDate);
        return response;
    }
}
