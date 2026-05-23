package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("InvoiceController Tests")
class InvoiceControllerTest extends BaseControllerTest {

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

    private Long buildPaymentAndGetId(BigDecimal amount) throws Exception {
        Building building = buildingRepository.save(new Building("Test Building", "Test Address"));
        Tenant tenant = tenantRepository.save(new Tenant("Juan", "Perez", "juaninvoice@test.com", "1122334455"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("1A");
        property.setArea(50.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, amount, LocalDate.now().plusDays(5));
        contract.setStatus(RentalContract.RentalContractStatus.PENDING);
        contract = rentalContractRepository.save(contract);

        Billing billing = new Billing();
        billing.setProperty(property);
        billing.setRentalContract(contract);
        billing.setPeriod("2026-05");
        billing.setRentAmount(amount);
        billing.setExpenses(BigDecimal.ZERO);
        billing.setAdditionalCharges(BigDecimal.ZERO);
        billing.setDebtAmount(BigDecimal.ZERO);
        billing.setTotalAmount(amount);
        billing.setDueDate(LocalDate.now().plusDays(5));
        billing.setStatus(Billing.BillingStatus.PENDING);
        billingRepository.save(billing);

        String body = "{\"amount\":" + amount.toPlainString() + ",\"paymentMethod\":\"BANK_TRANSFER\"," + "\"paymentDate\":\"" + LocalDate.now() + "\",\"selectedPeriods\":[\"2026-05\"]}";

        String response = mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments").contentType(MediaType.APPLICATION_JSON).content(body)).andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(response, "$.data.id")).longValue();
    }

    // ── GET /api/v1/payments/{paymentId}/receipt ───────────────────────────────

    @Test
    void test00_getReceipt_withValidPaymentId_returnsPdf() throws Exception {
        Long paymentId = buildPaymentAndGetId(new BigDecimal("1000.00"));

        mockMvc.perform(get("/api/v1/payments/" + paymentId + "/receipt")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF)).andExpect(header().string("Content-Disposition", containsString("recibo-" + paymentId + ".pdf"))).andExpect(result -> {
            byte[] body = result.getResponse().getContentAsByteArray();
            assert body.length > 0 : "PDF body should not be empty";
        });
    }

    @Test
    void test01_getReceipt_withNonExistentPaymentId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/payments/99999/receipt")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/v1/payments/export ────────────────────────────────────────────

    @Test
    void test02_exportPayments_withExistingPayments_returnsExcel() throws Exception {
        buildPaymentAndGetId(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/v1/payments/export")).andExpect(status().isOk()).andExpect(content().contentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).andExpect(header().string("Content-Disposition", containsString("pagos.xlsx"))).andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    assert body.length > 0 : "Excel body should not be empty";
                });
    }

    @Test
    void test03_exportPayments_withNoPayments_returnsEmptyExcel() throws Exception {
        mockMvc.perform(get("/api/v1/payments/export")).andExpect(status().isOk()).andExpect(content().contentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    assert body.length > 0 : "Excel with headers should not be empty";
                });
    }
}
