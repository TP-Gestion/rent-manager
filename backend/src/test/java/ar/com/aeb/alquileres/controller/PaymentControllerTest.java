package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

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

    private static int counter = 0;

    private Property buildProperty() {
        Building building = buildingRepository.save(new Building("TORRE CENTRAL", "Av. Corrientes 1234"));
        counter++;
        Tenant tenant = tenantRepository.save(new Tenant(
                "Tenant" + counter, "Test", "tenant" + counter + "@test.com",
                "11" + String.format("%08d", counter)));

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

        String body = "{\"amount\":100000,\"paymentMethod\":\"BANK_TRANSFER\",\"paymentDate\":\"2026-04-01\","
                + "\"reference\":\"TX123456789\",\"notes\":\"Pago correspondiente a marzo\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.amount").value(100000))
                .andExpect(jsonPath("$.data.paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.data.reference").value("TX123456789"))
                .andExpect(jsonPath("$.data.periods", hasItem("2026-03")));

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

        String body = "{\"amount\":200000,\"paymentMethod\":\"BANK_TRANSFER\",\"paymentDate\":\"2026-05-01\","
                + "\"reference\":\"TX987654321\",\"notes\":\"Pago de deuda acumulada\","
                + "\"selectedPeriods\":[\"2026-02\",\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.periods", hasItems("2026-02", "2026-03")));

        // Selected billings → PAID
        assert billingRepository.findById(b1.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PAID;
        assert billingRepository.findById(b2.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PAID;
        // Other billing → unchanged
        assert billingRepository.findById(b3.getId()).orElseThrow().getStatus() == Billing.BillingStatus.PENDING;
    }

    @Test
    void test02_registerPayment_emptySelectedPeriods_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        String body = "{\"amount\":100000,\"paymentMethod\":\"BANK_TRANSFER\","
                + "\"paymentDate\":\"2026-04-01\",\"selectedPeriods\":[]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test03_registerPayment_missingAmount_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        String body = "{\"paymentMethod\":\"BANK_TRANSFER\",\"paymentDate\":\"2026-04-01\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test04_registerPayment_missingPaymentMethod_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        String body = "{\"amount\":100000,\"paymentDate\":\"2026-04-01\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test05_registerPayment_missingPaymentDate_returnsBadRequest() throws Exception {
        Property property = buildProperty();

        String body = "{\"amount\":100000,\"paymentMethod\":\"CASH\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test06_registerPayment_billingAlreadyPaid_returnsUnprocessableEntity() throws Exception {
        Property property = buildProperty();
        buildBilling(property, "2026-03", Billing.BillingStatus.PAID);

        String body = "{\"amount\":100000,\"paymentMethod\":\"CASH\",\"paymentDate\":\"2026-04-01\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void test07_registerPayment_propertyNotFound_returnsNotFound() throws Exception {
        String body = "{\"amount\":100000,\"paymentMethod\":\"CASH\",\"paymentDate\":\"2026-04-01\","
                + "\"selectedPeriods\":[\"2026-03\"]}";

        mockMvc.perform(post("/api/v1/properties/99999/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/v1/properties/{id}/payments ───────────────────────────────────

    @Test
    void test08_getPayments_returnsListWithPeriodsArray() throws Exception {
        // Setup: register a payment via the endpoint
        Property property = buildProperty();
        buildBilling(property, "2026-03", Billing.BillingStatus.OVERDUE);

        String body = "{\"amount\":100000,\"paymentMethod\":\"BANK_TRANSFER\","
                + "\"paymentDate\":\"2026-04-01\",\"reference\":\"TX123\","
                + "\"selectedPeriods\":[\"2026-03\"]}";
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Execute & Verify
        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].date").value("2026-04-01"))
                .andExpect(jsonPath("$.data[0].amount").value(100000))
                .andExpect(jsonPath("$.data[0].paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.data[0].reference").value("TX123"))
                .andExpect(jsonPath("$.data[0].periods", hasItem("2026-03")));
    }

    @Test
    void test09_getPayments_propertyNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/properties/99999/payments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
