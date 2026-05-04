package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
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
        buildPropertyWithContract(RentalContract.RentalContractStatus.PENDING, new BigDecimal("180000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].unit").exists())
                .andExpect(jsonPath("$.data[0].building").exists())
                .andExpect(jsonPath("$.data[0].address").exists())
                .andExpect(jsonPath("$.data[0].tenant").exists())
                .andExpect(jsonPath("$.data[0].previousStatus").value("PENDING"))
                .andExpect(jsonPath("$.data[0].rentAmount").value(180000))
                .andExpect(jsonPath("$.data[0].totalAmount").exists())
                .andExpect(jsonPath("$.data[0].dueDate").exists())
                .andExpect(jsonPath("$.data[0].period").exists());
    }

    @Test
    void test04_getBillable_noBillableProperties_returnsEmptyList() throws Exception {
        buildPropertyWithContract(RentalContract.RentalContractStatus.PAID, new BigDecimal("100000"));

        mockMvc.perform(get("/api/v1/properties/billable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
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
}
