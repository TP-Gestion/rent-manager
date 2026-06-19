package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PaymentController Tests")
class PaymentControllerTest extends BaseControllerTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static int counter = 0;

    /** Build a property occupied by a tenant with the given name. */
    private Property buildPropertyWithTenant(String firstName, String lastName) {
        Building building = buildingRepository.save(new Building("TORRE CENTRAL", "Av. Corrientes 1234"));
        counter++;
        Tenant tenant = tenantRepository.save(new Tenant(
                firstName, lastName, firstName.toLowerCase() + counter + "@test.com", "11" + String.format("%08d", counter)));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("4B");
        property.setArea(60.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        return propertyRepository.save(property);
    }

    /** Register a payment snapshotting the property's current tenant (mirrors PaymentService). */
    private Payment registerPaymentFor(Property property) {
        Payment payment = new Payment();
        payment.setProperty(property);
        payment.setTenant(property.getTenant());
        payment.setPaymentDate(LocalDate.parse("2026-06-06"));
        payment.setAmount(new BigDecimal("111111.00"));
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        return paymentRepository.save(payment);
    }

    private Property buildProperty() {
        Building building = buildingRepository.save(new Building("TORRE CENTRAL", "Av. Corrientes 1234"));
        counter++;
        Tenant tenant = tenantRepository.save(new Tenant(
                "Tenant" + counter, "Test", "tenant" + counter + "@test.com", "11" + String.format("%08d", counter)));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("3B");
        property.setArea(60.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        return propertyRepository.save(property);
    }

    private Billing buildBilling(Property property, String period, Billing.BillingStatus status) {
        RentalContract contract = new RentalContract(property, new BigDecimal("100000"), LocalDate.now().plusDays(10));
        contract.setStatus(RentalContract.RentalContractStatus.PENDING);
        contract = rentalContractRepository.save(contract);

        Billing billing = new Billing();
        billing.setProperty(property);
        billing.setTenant(property.getTenant());
        billing.setRentalContract(contract);
        billing.setPeriod(period);
        billing.setRentAmount(new BigDecimal("100000"));
        billing.setExpenses(BigDecimal.ZERO);
        billing.setAdditionalCharges(BigDecimal.ZERO);
        billing.setDebtAmount(BigDecimal.ZERO);
        billing.setTotalAmount(new BigDecimal("100000"));
        billing.setDueDate(LocalDate.now().plusDays(10));
        billing.setStatus(status);
        return billingRepository.save(billing);
    }

    // ── POST /api/v1/properties/{id}/payments ──────────────────────────────────

    @Test
    void test00_registerPayment_singlePeriod_billingMarkedAsPaid() throws Exception {
        Property property = buildProperty();
        Billing billing = buildBilling(property, "2026-03", Billing.BillingStatus.OVERDUE);

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentMethod", "BANK_TRANSFER").param("paymentDate", "2026-04-01").param("reference", "TX123456789").param("notes", "Pago correspondiente a marzo").param("selectedPeriods", "2026-03")).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value(201)).andExpect(jsonPath("$.data.amount").value(100000)).andExpect(jsonPath("$.data.paymentMethod").value("BANK_TRANSFER")).andExpect(jsonPath("$.data.reference").value("TX123456789")).andExpect(jsonPath("$.data.periods", hasItem("2026-03")));

        // Verify billing marked as PAID in DB
        Billing updated = billingRepository.findById(billing.getId()).orElseThrow();
        assert updated.getStatus() == Billing.BillingStatus.PAID;
    }

    @Test
    void test01_registerPayment_multiplePeriods_selectedBillingsPaid_otherUnchanged() throws Exception {
        Property property = buildProperty();
        Billing b1 = buildBilling(property, "2026-02", Billing.BillingStatus.OVERDUE);
        Billing b2 = buildBilling(property, "2026-03", Billing.BillingStatus.OVERDUE);
        Billing b3 = buildBilling(property, "2026-04", Billing.BillingStatus.PENDING);

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "200000").param("paymentMethod", "BANK_TRANSFER").param("paymentDate", "2026-05-01").param("reference", "TX987654321").param("notes", "Pago de deuda acumulada").param("selectedPeriods", "2026-02", "2026-03")).andExpect(status().isCreated()).andExpect(jsonPath("$.data.periods", hasItems("2026-02", "2026-03")));

        // Selected billings → PAID
        assert billingRepository.findById(b1.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PAID;
        assert billingRepository.findById(b2.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PAID;
        // Other billing → unchanged
        assert billingRepository.findById(b3.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PENDING;
    }

    @Test
    void test02_registerPayment_emptySelectedPeriods_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentMethod", "BANK_TRANSFER").param("paymentDate", "2026-04-01")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test03_registerPayment_missingAmount_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("paymentMethod", "BANK_TRANSFER").param("paymentDate", "2026-04-01").param("selectedPeriods", "2026-03")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test04_registerPayment_missingPaymentMethod_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentDate", "2026-04-01").param("selectedPeriods", "2026-03")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test05_registerPayment_missingPaymentDate_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentMethod", "CASH").param("selectedPeriods", "2026-03")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test06_registerPayment_billingAlreadyPaid_returnsUnprocessableEntity() throws Exception {
        Property property = buildProperty();
        buildBilling(property, "2026-03", Billing.BillingStatus.PAID);

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentMethod", "CASH").param("paymentDate", "2026-04-01").param("selectedPeriods", "2026-03")).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void test07_registerPayment_propertyNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(multipart("/api/v1/properties/99999/payments").param("amount", "100000").param("paymentMethod", "CASH").param("paymentDate", "2026-04-01").param("selectedPeriods", "2026-03")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/v1/properties/{id}/payments ───────────────────────────────────

    @Test
    void test08_getPayments_returnsListWithPeriodsArray() throws Exception {
        // Setup: register a payment via the endpoint
        Property property = buildProperty();
        buildBilling(property, "2026-03", Billing.BillingStatus.OVERDUE);

        mockMvc.perform(multipart("/api/v1/properties/" + property.getId() + "/payments").param("amount", "100000").param("paymentMethod", "BANK_TRANSFER").param("paymentDate", "2026-04-01").param("reference", "TX123").param("selectedPeriods", "2026-03"));

        // Execute & Verify
        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data").isArray()).andExpect(jsonPath("$.data.length()").value(1)).andExpect(jsonPath("$.data[0].date").value("2026-04-01")).andExpect(jsonPath("$.data[0].amount").value(100000)).andExpect(jsonPath("$.data[0].paymentMethod").value("BANK_TRANSFER")).andExpect(jsonPath("$.data[0].reference").value("TX123")).andExpect(jsonPath("$.data[0].periods", hasItem("2026-03")));
    }

    @Test
    void test09_getPayments_propertyNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/properties/99999/payments")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    // ── Regla 3: el historial muestra el inquilino que realizó cada pago ─────────

    @Test
    void test10_getPayments_includesHistoricalTenant() throws Exception {
        Property property = buildPropertyWithTenant("Juan", "Pérez");
        registerPaymentFor(property);

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].tenant.firstName").value("Juan")).andExpect(jsonPath("$.data[0].tenant.lastName").value("Pérez"));
    }

    // ── Regla 1: desvincular inquilino sin perder historial ─────────────────────

    @Test
    void test11_removeTenant_propertyFreed_paymentKeepsHistoricalTenant() throws Exception {
        Property property = buildPropertyWithTenant("Juan", "Pérez");
        registerPaymentFor(property);

        // Cuando elimino al inquilino de la propiedad
        mockMvc.perform(delete("/api/v1/properties/" + property.getId() + "/tenant")).andExpect(status().isOk());

        // Entonces: la propiedad queda sin inquilino asignado
        Property reloaded = propertyRepository.findById(property.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertNull(reloaded.getTenant());
        org.junit.jupiter.api.Assertions.assertEquals(Property.OccupancyStatus.AVAILABLE, reloaded.getOccupancyStatus());

        // Y el historial sigue mostrando que el pago fue realizado por "Juan Pérez"
        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments")).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1)).andExpect(jsonPath("$.data[0].tenant.firstName").value("Juan")).andExpect(jsonPath("$.data[0].tenant.lastName").value("Pérez"));
    }

    // ── Regla 2: asignar nuevo inquilino conservando historial ──────────────────

    @Test
    void test12_reassignTenant_paymentStillShowsOriginalTenant() throws Exception {
        Property property = buildPropertyWithTenant("Juan", "Pérez");
        registerPaymentFor(property);

        // Elimino a "Juan Pérez"
        mockMvc.perform(delete("/api/v1/properties/" + property.getId() + "/tenant")).andExpect(status().isOk());

        // Y posteriormente asigno a "María Gómez"
        Tenant maria = tenantRepository.save(new Tenant("María", "Gómez", "maria" + (++counter) + "@test.com", "11" + String.format("%08d", counter)));
        mockMvc.perform(patch("/api/v1/properties/" + property.getId() + "/tenant/" + maria.getId())).andExpect(status().isOk());

        // La propiedad queda asociada a María, pero el pago histórico sigue mostrando "Juan Pérez"
        Property reloaded = propertyRepository.findById(property.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("María", reloaded.getTenant().getFirstName());

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].tenant.firstName").value("Juan")).andExpect(jsonPath("$.data[0].tenant.lastName").value("Pérez"));
    }

    // ── Comentario backend: pagos históricos sin inquilino (legacy) → tenant null ─

    @Test
    void test13_getPayments_legacyPaymentWithoutTenant_returnsNullTenant() throws Exception {
        Property property = buildPropertyWithTenant("Juan", "Pérez");
        Payment payment = registerPaymentFor(property);
        payment.setTenant(null); // simula un pago anterior a la feature
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].tenant").doesNotExist());
    }

    // ── GET /billings: muestra el inquilino al que se le facturó ─────────────────

    @Test
    void test14_getBillings_includesBilledTenant_keptAfterTenantChange() throws Exception {
        Property property = buildPropertyWithTenant("Juan", "Pérez");
        buildBilling(property, "2026-06", Billing.BillingStatus.PENDING);

        // El billing muestra al inquilino facturado
        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/billings")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].tenant.firstName").value("Juan")).andExpect(jsonPath("$.data[0].tenant.lastName").value("Pérez"));

        // Tras desvincular al inquilino, el billing histórico sigue mostrando "Juan Pérez"
        mockMvc.perform(delete("/api/v1/properties/" + property.getId() + "/tenant")).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/billings")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].tenant.firstName").value("Juan")).andExpect(jsonPath("$.data[0].tenant.lastName").value("Pérez"));
    }
}
