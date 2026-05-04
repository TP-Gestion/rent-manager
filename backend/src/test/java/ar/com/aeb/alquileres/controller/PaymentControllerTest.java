package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private Property buildPropertyWithPendingContract(BigDecimal contractAmount) {
        Building building = buildingRepository.save(new Building("Test Building", "Test Address"));
        Tenant tenant = tenantRepository.save(new Tenant("Juan", "Perez", "juan@test.com", "1122334455"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("1A");
        property.setArea(50.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, contractAmount, LocalDate.now().plusDays(5));
        contract.setStatus(RentalContract.RentalContractStatus.PENDING);
        rentalContractRepository.save(contract);

        return property;
    }

    // ── POST /api/v1/properties/{propertyId}/payments ──────────────────────────

    @Test
    void test00_registerPayment_withValidData_returnsCreated() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"TRANSFER\"}";

        // Execute & Verify
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.totalAmount").value(1000.00))
                .andExpect(jsonPath("$.data.paymentMethod").value("TRANSFER"))
                .andExpect(jsonPath("$.data.propertyId").value(property.getId()));

        // Verify contract marked as PAID in DB
        RentalContract contract = rentalContractRepository.findByPropertyId(property.getId()).get(0);
        assert contract.getStatus() == RentalContract.RentalContractStatus.PAID;
    }

    @Test
    void test01_registerPayment_withNonExistentProperty_returnsNotFound() throws Exception {
        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/99999/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void test02_registerPayment_withNoPendingContract_returnsUnprocessableEntity() throws Exception {
        // Setup: property with no rental contract
        Building building = buildingRepository.save(new Building("Building 2", "Address 2"));
        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("2B");
        property.setArea(40.0);
        property.setRooms(1);
        property.setUnitType("Studio");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void test03_registerPayment_withAlreadyPaidContract_returnsUnprocessableEntity() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));
        RentalContract contract = rentalContractRepository.findByPropertyId(property.getId()).get(0);
        contract.setStatus(RentalContract.RentalContractStatus.PAID);
        rentalContractRepository.save(contract);

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void test04_registerPayment_withIncorrectAmount_returnsUnprocessableEntity() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":500.00,\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void test05_registerPayment_withMissingPaymentDate_returnsBadRequest() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        String body = "{\"totalAmount\":1000.00,\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test06_registerPayment_withMissingPaymentMethod_returnsBadRequest() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test07_registerPayment_withMissingTotalAmount_returnsBadRequest() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"paymentMethod\":\"CASH\"}";

        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ── GET /api/v1/properties/{propertyId}/payments ───────────────────────────

    @Test
    void test08_getPaymentsByProperty_withExistingPayments_returnsOk() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));
        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"TRANSFER\"}";
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].paymentMethod").value("TRANSFER"));
    }

    @Test
    void test09_getPaymentsByProperty_withNoPayments_returnsEmptyList() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));

        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void test10_getPaymentsByProperty_withNonExistentProperty_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/properties/99999/payments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/v1/payments ───────────────────────────────────────────────────

    @Test
    void test11_getAllPayments_returnsOk() throws Exception {
        Property property = buildPropertyWithPendingContract(new BigDecimal("1000.00"));
        String body = "{\"paymentDate\":\"" + LocalDate.now() + "\",\"totalAmount\":1000.00,\"paymentMethod\":\"CHECK\"}";
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}
