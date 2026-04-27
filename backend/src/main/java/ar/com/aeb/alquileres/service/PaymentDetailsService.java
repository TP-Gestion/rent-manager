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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        // Get rental contract for this property
        RentalContractResponse rentalContractResponse = null;
        List<RentalContract> contracts = rentalContractRepository.findByPropertyId(property.getId());
        if (!contracts.isEmpty()) {
            RentalContract contract = contracts.get(0);
            rentalContractResponse = new RentalContractResponse(contract);
        }

        PaymentDetailsResponse response = new PaymentDetailsResponse(rentalContractResponse, expenseDetails);
        return response;
    }
}
