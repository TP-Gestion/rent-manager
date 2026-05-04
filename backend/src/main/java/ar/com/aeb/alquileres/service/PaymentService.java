package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.payment.PaymentRequest;
import ar.com.aeb.alquileres.dto.payment.PaymentResponse;
import ar.com.aeb.alquileres.exception.payment.BillingAlreadyPaidException;
import ar.com.aeb.alquileres.exception.payment.BillingPeriodNotFoundException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.model.Payment;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.repository.BillingRepository;
import ar.com.aeb.alquileres.repository.PaymentRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BillingRepository billingRepository;

    public PaymentResponse registerPayment(Long propertyId, PaymentRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        List<Billing> billingsToPay = new ArrayList<>();
        for (String period : request.getSelectedPeriods()) {
            Billing billing = billingRepository.findByPropertyIdAndPeriod(propertyId, period)
                    .orElseThrow(() -> new BillingPeriodNotFoundException(propertyId, period));

            if (billing.getStatus() == Billing.BillingStatus.PAID) {
                throw new BillingAlreadyPaidException(period);
            }
            billingsToPay.add(billing);
        }

        Payment payment = new Payment();
        payment.setProperty(property);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());
        payment = paymentRepository.save(payment);

        for (Billing billing : billingsToPay) {
            billing.setStatus(Billing.BillingStatus.PAID);
            billing.setPayment(payment);
            billingRepository.save(billing);
            payment.getBillings().add(billing);
        }

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
