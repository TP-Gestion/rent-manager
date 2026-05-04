package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("BillingController Tests")
class BillingControllerTest extends BaseControllerTest {

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

    private static int tenantCounter = 0;

    private Property buildPropertyWithContract(RentalContract.RentalContractStatus status, BigDecimal amount) {
        Building building = buildingRepository.save(new Building("TORRE CENTRAL", "Av. Corrientes 1234"));
        tenantCounter++;
        Tenant tenant = tenantRepository.save(new Tenant(
                "Inquilino" + tenantCounter, "Apellido" + tenantCounter,
                "tenant" + tenantCounter + "@test.com", "11" + String.format("%08d", tenantCounter)));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("3B");
        property.setArea(60.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, amount, LocalDate.now().plusDays(10));
        contract.setStatus(status);
        rentalContractRepository.save(contract);

        return property;
    }

    // ── GET /api/v1/properties/billable ────────────────────────────────────────

    @Test
    void test00_getBillable_propertyWithPendingContract_appearsInList() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("150000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].id", hasItem(property.getId().intValue())));
    }

    @Test
    void test01_getBillable_propertyWithOverdueContract_appearsInList() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.OVERDUE, new BigDecimal("120000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", hasItem(property.getId().intValue())));
    }

    @Test
    void test02_getBillable_propertyWithPaidContract_doesNotAppear() throws Exception {
        Property paidProperty = buildPropertyWithContract(RentalContract.RentalContractStatus.PAID, new BigDecimal("100000"));
        Property pendingProperty = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("100000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", not(hasItem(paidProperty.getId().intValue()))))
                .andExpect(jsonPath("$.data[*].id", hasItem(pendingProperty.getId().intValue())));
    }

    @Test
    void test03_getBillable_responseHasCorrectFields() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("180000"));
        String idFilter = "$.data[?(@.id == " + property.getId() + ")]";

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(idFilter + ".unit").exists())
                .andExpect(jsonPath(idFilter + ".building").exists())
                .andExpect(jsonPath(idFilter + ".address").exists())
                .andExpect(jsonPath(idFilter + ".tenant").exists())
                .andExpect(jsonPath(idFilter + ".previousStatus", hasItem("PENDING")))
                .andExpect(jsonPath(idFilter + ".rentAmount", hasItem(180000)))
                .andExpect(jsonPath(idFilter + ".totalAmount").exists())
                .andExpect(jsonPath(idFilter + ".dueDate").exists())
                .andExpect(jsonPath(idFilter + ".period").exists());
    }

    @Test
    void test04_getBillable_paidPropertyDoesNotAppearEvenWithOtherBillables() throws Exception {
        Property paidProperty = buildPropertyWithContract(RentalContract.RentalContractStatus.PAID, new BigDecimal("100000"));
        buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("100000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].id", not(hasItem(paidProperty.getId().intValue()))));
    }

    // ── POST /api/v1/billings ──────────────────────────────────────────────────

    @Test
    void test05_createBillings_paidProperty_createsBillingWithPendingStatus() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PAID, new BigDecimal("150000"));

        String body = "{\"propertyIds\":[" + property.getId() + "]}";

        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.count").value(1));

        // Verify new contract status is PENDING
        RentalContract latest = rentalContractRepository
                .findByPropertyId(property.getId())
                .stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .orElseThrow();
        assert latest.getStatus() == RentalContract.RentalContractStatus.PENDING;
    }

    @Test
    void test06_createBillings_pendingProperty_createsBillingWithOverdueStatus() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("120000"));

        String body = "{\"propertyIds\":[" + property.getId() + "]}";

        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.count").value(1));

        RentalContract latest = rentalContractRepository
                .findByPropertyId(property.getId())
                .stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .orElseThrow();
        assert latest.getStatus() == RentalContract.RentalContractStatus.OVERDUE;
    }

    @Test
    void test07_createBillings_overdueProperty_createsBillingWithOverdueStatus() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.OVERDUE, new BigDecimal("180000"));

        String body = "{\"propertyIds\":[" + property.getId() + "]}";

        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.count").value(1));

        RentalContract latest = rentalContractRepository
                .findByPropertyId(property.getId())
                .stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .orElseThrow();
        assert latest.getStatus() == RentalContract.RentalContractStatus.OVERDUE;
    }

    @Test
    void test08_createBillings_multipleProperties_returnsCorrectCount() throws Exception {
        Property p1 = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("100000"));
        Property p2 = buildPropertyWithContract(RentalContract.RentalContractStatus.OVERDUE, new BigDecimal("200000"));
        Property p3 = buildPropertyWithContract(RentalContract.RentalContractStatus.PAID, new BigDecimal("150000"));

        String body = "{\"propertyIds\":[" + p1.getId() + "," + p2.getId() + "," + p3.getId() + "]}";

        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.count").value(3));
    }

    @Test
    void test09_createBillings_emptyList_returnsZeroCount() throws Exception {
        String body = "{\"propertyIds\":[]}";

        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.count").value(0));
    }

    // ── GET /api/v1/billings/export ────────────────────────────────────────────

    @Test
    void test10_exportBillings_withExistingBillings_returnsExcel() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("150000"));
        String body = "{\"propertyIds\":[" + property.getId() + "]}";
        mockMvc.perform(post("/api/v1/billings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        mockMvc.perform(get("/api/v1/billings/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition",
                        containsString("facturas.xlsx")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    assert bytes.length > 0;
                });
    }

    @Test
    void test11_exportBillings_noBillings_returnsEmptyExcel() throws Exception {
        mockMvc.perform(get("/api/v1/billings/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    assert bytes.length > 0;
                });
    }

    // ── GET /api/v1/properties/{id}/billings ──────────────────────────────────

    private Billing buildBillingDirectly(Property property, String period, Billing.BillingStatus status) {
        RentalContract contract = new RentalContract(property, new BigDecimal("150000"), LocalDate.now().plusDays(10));
        contract.setStatus(RentalContract.RentalContractStatus.PENDING);
        contract = rentalContractRepository.save(contract);

        Billing billing = new Billing();
        billing.setProperty(property);
        billing.setRentalContract(contract);
        billing.setPeriod(period);
        billing.setRentAmount(new BigDecimal("150000"));
        billing.setExpenses(BigDecimal.ZERO);
        billing.setAdditionalCharges(BigDecimal.ZERO);
        billing.setDebtAmount(BigDecimal.ZERO);
        billing.setTotalAmount(new BigDecimal("150000"));
        billing.setDueDate(LocalDate.now().plusDays(10));
        billing.setStatus(status);
        return billingRepository.save(billing);
    }

    @Test
    void test12_getBillingsByProperty_returnsListWithCorrectFields() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("150000"));
        buildBillingDirectly(property, "2026-04", Billing.BillingStatus.PENDING);

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/billings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.period == '2026-04')].id").exists())
                .andExpect(jsonPath("$.data[?(@.period == '2026-04')].status", hasItem("PENDING")))
                .andExpect(jsonPath("$.data[?(@.period == '2026-04')].amount").exists())
                .andExpect(jsonPath("$.data[?(@.period == '2026-04')].dueDate").exists());
    }

    @Test
    void test13_getBillingsByProperty_paidBillingIncludesPaymentDate() throws Exception {
        Property property = buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("150000"));
        buildBillingDirectly(property, "2026-03", Billing.BillingStatus.PENDING);

        // Register payment to mark billing as PAID
        String paymentBody = "{\"amount\":150000,\"paymentMethod\":\"BANK_TRANSFER\","
                + "\"paymentDate\":\"2026-04-01\",\"selectedPeriods\":[\"2026-03\"]}";
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentBody));

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/billings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.period == '2026-03')].status", hasItem("PAID")))
                .andExpect(jsonPath("$.data[?(@.period == '2026-03')].paymentDate", hasItem("2026-04-01")));
    }

    @Test
    void test14_getBillingsByProperty_propertyNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/properties/99999/billings"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
