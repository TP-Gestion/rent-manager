package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.billing.BillablePropertyResponse;
import ar.com.aeb.alquileres.dto.billing.BillableTenantResponse;
import ar.com.aeb.alquileres.dto.billing.BillingCountResponse;
import ar.com.aeb.alquileres.dto.billing.BillingRequest;
import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BillingService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private PropertyExpenseRepository propertyExpenseRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Transactional(readOnly = true)
    public List<BillablePropertyResponse> getBillableProperties() {
        return propertyRepository.findAll().stream()
                .filter(p -> getLatestContract(p.getId())
                        .map(c -> c.getStatus() == RentalContract.RentalContractStatus.PENDING
                                || c.getStatus() == RentalContract.RentalContractStatus.OVERDUE)
                        .orElse(false))
                .map(this::buildBillableResponse)
                .toList();
    }

    public BillingCountResponse createBillings(BillingRequest request) {
        if (request.getPropertyIds() == null || request.getPropertyIds().isEmpty()) {
            return new BillingCountResponse(0);
        }

        int count = 0;
        for (Long propertyId : request.getPropertyIds()) {
            Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
            if (propertyOpt.isEmpty()) continue;

            Optional<RentalContract> contractOpt = getLatestContract(propertyId);
            if (contractOpt.isEmpty()) continue;

            Property property = propertyOpt.get();
            RentalContract contract = contractOpt.get();
            RentalContract.RentalContractStatus previousStatus = contract.getStatus();

            RentalContract.RentalContractStatus newStatus = switch (previousStatus) {
                case PAID -> RentalContract.RentalContractStatus.PENDING;
                case PENDING, OVERDUE -> RentalContract.RentalContractStatus.OVERDUE;
            };

            contract.setStatus(newStatus);
            rentalContractRepository.save(contract);

            BigDecimal expenses = getPendingExpenses(propertyId);
            BigDecimal debtAmount = previousStatus == RentalContract.RentalContractStatus.PAID
                    ? BigDecimal.ZERO
                    : contract.getAmount();
            BigDecimal totalAmount = contract.getAmount().add(expenses);
            String period = YearMonth.from(contract.getDueDate()).toString();

            Billing billing = new Billing();
            billing.setProperty(property);
            billing.setRentalContract(contract);
            billing.setPeriod(period);
            billing.setRentAmount(contract.getAmount());
            billing.setExpenses(expenses);
            billing.setAdditionalCharges(BigDecimal.ZERO);
            billing.setDebtAmount(debtAmount);
            billing.setTotalAmount(totalAmount);
            billing.setDueDate(contract.getDueDate());
            billing.setStatus(newStatus == RentalContract.RentalContractStatus.PENDING
                    ? Billing.BillingStatus.PENDING
                    : Billing.BillingStatus.OVERDUE);
            billingRepository.save(billing);

            count++;
        }

        return new BillingCountResponse(count);
    }

    @Transactional(readOnly = true)
    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    private BillablePropertyResponse buildBillableResponse(Property property) {
        RentalContract contract = getLatestContract(property.getId()).orElseThrow();

        BigDecimal expenses = getPendingExpenses(property.getId());
        BigDecimal debtAmount = calculateAccumulatedDebt(property.getId());
        BigDecimal totalAmount = contract.getAmount().add(expenses);
        String period = YearMonth.from(contract.getDueDate()).toString();

        BillablePropertyResponse response = new BillablePropertyResponse();
        response.setId(property.getId());
        response.setUnit(property.getFloor());
        response.setBuilding(property.getBuilding().getName());
        response.setAddress(property.getBuilding().getAddress());
        response.setTenant(new BillableTenantResponse(property.getTenant()));
        response.setPreviousStatus(contract.getStatus().name());
        response.setDebtAmount(debtAmount);
        response.setRentAmount(contract.getAmount());
        response.setExpenses(expenses);
        response.setAdditionalCharges(BigDecimal.ZERO);
        response.setTotalAmount(totalAmount);
        response.setDueDate(contract.getDueDate());
        response.setPeriod(period);
        return response;
    }

    private Optional<RentalContract> getLatestContract(Long propertyId) {
        return rentalContractRepository.findByPropertyId(propertyId).stream()
                .max(Comparator.comparing(BaseEntity::getCreatedAt));
    }

    private BigDecimal getPendingExpenses(Long propertyId) {
        return propertyExpenseRepository.findByPropertyId(propertyId).stream()
                .filter(pe -> pe.getStatus() == PropertyExpense.PropertyExpenseStatus.PENDING)
                .map(PropertyExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAccumulatedDebt(Long propertyId) {
        return billingRepository.findByPropertyId(propertyId).stream()
                .filter(b -> b.getStatus() == Billing.BillingStatus.PENDING
                        || b.getStatus() == Billing.BillingStatus.OVERDUE)
                .map(Billing::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
