package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import ar.com.aeb.alquileres.repository.PropertyExpenseRepository;
import ar.com.aeb.alquileres.repository.BillingRepository;
import ar.com.aeb.alquileres.dto.billing.BillablePropertyResponse;
import ar.com.aeb.alquileres.dto.billing.BillableTenantResponse;
import ar.com.aeb.alquileres.dto.billing.BillingCountResponse;
import ar.com.aeb.alquileres.dto.billing.BillingRequest;
import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import ar.com.aeb.alquileres.model.BaseEntity;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;

import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.io.ByteArrayOutputStream;
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

    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public List<BillablePropertyResponse> getBillableProperties() {
        return propertyRepository.findAll().stream().filter(p -> getLatestContract(p.getId()).map(c -> c.getStatus() == RentalContract.RentalContractStatus.PENDING || c.getStatus() == RentalContract.RentalContractStatus.OVERDUE).orElse(false)).map(this::buildBillableResponse).toList();
    }

    public BillingCountResponse createBillings(BillingRequest request) {
        if (request.getPropertyIds() == null || request.getPropertyIds().isEmpty()) {
            return new BillingCountResponse(0);
        }

        int count = 0;
        for (Long propertyId : request.getPropertyIds()) {
            Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
            if (propertyOpt.isEmpty()) {
                Property property = propertyRepository.findById(propertyId)
                        .orElseThrow(() -> new IllegalArgumentException("Property with ID " + propertyId + " not found"));
            };

            Optional<RentalContract> contractOpt = getLatestContract(propertyId);
            if (contractOpt.isEmpty()) {
                RentalContract contract = getLatestContract(propertyId)
                        .orElseThrow(() -> new IllegalArgumentException("No active rental contract found for property ID " + propertyId));
            };

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
            BigDecimal debtAmount = previousStatus == RentalContract.RentalContractStatus.PAID ? BigDecimal.ZERO : contract.getAmount();
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
            billing.setStatus(newStatus == RentalContract.RentalContractStatus.PENDING ? Billing.BillingStatus.PENDING : Billing.BillingStatus.OVERDUE);
            billingRepository.save(billing);

            Tenant tenant = contract.getTenant();
            byte[] pdf = generatePdf(tenant, property, contract);
            emailService.sendBillingEmail(tenant.getEmail(), pdf);

            // mark as notified
            billing.setNotified(true);
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
        return rentalContractRepository.findByPropertyId(propertyId).stream().max(Comparator.comparing(BaseEntity::getCreatedAt));
    }

    private BigDecimal getPendingExpenses(Long propertyId) {
        return propertyExpenseRepository.findByPropertyId(propertyId).stream().filter(pe -> pe.getStatus() == PropertyExpense.PropertyExpenseStatus.PENDING).map(PropertyExpense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAccumulatedDebt(Long propertyId) {
        return billingRepository.findByPropertyId(propertyId).stream().filter(b -> b.getStatus() == Billing.BillingStatus.PENDING || b.getStatus() == Billing.BillingStatus.OVERDUE).map(Billing::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private byte[] generatePdf(Tenant tenant, Property property, RentalContract contract) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Encabezado
        document.add(new Paragraph("FACTURA").setBold().setFontSize(16));

        document.add(new Paragraph("Fecha de emisión: " + contract.getDueDate().toString()));
        document.add(new Paragraph("Emisor: Sistema de Alquileres"));
        document.add(new Paragraph("Cliente: " + tenant.getFirstName() + " " + tenant.getLastName()));
        document.add(new Paragraph("Email: " + tenant.getEmail()));
        document.add(new Paragraph("Unidad: " + property.getFloor() + " - " + property.getBuilding().getName()));
        document.add(new Paragraph("Dirección: " + property.getBuilding().getAddress()));

        document.add(new Paragraph("\n"));

        // Tabla con detalles
        Table table = new Table(2);
        table.addCell("Descripción");
        table.addCell("Monto");

        table.addCell("Renta");
        table.addCell(contract.getAmount().toString());

        table.addCell("Gastos");
        table.addCell(getPendingExpenses(property.getId()).toString());

        table.addCell("Deuda acumulada");
        table.addCell(calculateAccumulatedDebt(property.getId()).toString());

        table.addCell("Total");
        table.addCell(contract.getAmount().add(getPendingExpenses(property.getId())).toString());

        document.add(table);

        document.close();
        return baos.toByteArray();
    }

    public int notifyExpiringContractsManual() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(7);

        List<RentalContract> contracts = rentalContractRepository.findByDueDateBetween(today, targetDate);
        int count = 0;

        for (RentalContract contract : contracts) {
            Tenant tenant = contract.getTenant();
            Property property = contract.getProperty();

            byte[] pdf = generatePdf(tenant, property, contract);
            emailService.sendBillingEmail(tenant.getEmail(), pdf);

            count++;
        }
        return count;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void notifyExpiringContractsScheduled() {
        notifyExpiringContractsManual();
    }
}
