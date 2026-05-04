package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.payment.PaymentRequest;
import ar.com.aeb.alquileres.dto.payment.PaymentResponse;
import ar.com.aeb.alquileres.exception.payment.InvalidPaymentAmountException;
import ar.com.aeb.alquileres.exception.payment.NoPendingContractException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Payment;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.repository.PaymentRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    public PaymentResponse registerPayment(Long propertyId, PaymentRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        RentalContract contract = rentalContractRepository
                .findByPropertyId(propertyId)
                .stream()
                .filter(c -> c.getStatus() == RentalContract.RentalContractStatus.PENDING
                        || c.getStatus() == RentalContract.RentalContractStatus.OVERDUE)
                .findFirst()
                .orElseThrow(() -> new NoPendingContractException(propertyId));

        if (request.getTotalAmount().compareTo(contract.getAmount()) != 0) {
            throw new InvalidPaymentAmountException(contract.getAmount(), request.getTotalAmount());
        }

        Payment payment = new Payment(property, contract, request.getPaymentDate(), request.getTotalAmount(), request.getPaymentMethod(), request.getNotes());
        payment = paymentRepository.save(payment);

        contract.setStatus(RentalContract.RentalContractStatus.PAID);
        rentalContractRepository.save(contract);

        return new PaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByProperty(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new PropertyNotFoundException(propertyId);
        }
        return paymentRepository.findByPropertyId(propertyId).stream()
                .map(PaymentResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponse::new)
                .toList();
    }
}
