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
    private PaymentRepository paymentRepository;

    private Payment buildPayment(BigDecimal amount) {
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

        RentalContract contract = new RentalContract(property, amount, LocalDate.now().plusDays(5));
        contract.setStatus(RentalContract.RentalContractStatus.PAID);
        contract = rentalContractRepository.save(contract);

        Payment payment = new Payment(property, contract, LocalDate.now(), amount, Payment.PaymentMethod.TRANSFER, null);
        return paymentRepository.save(payment);
    }

    // ── GET /api/v1/payments/{paymentId}/receipt ───────────────────────────────

    @Test
    void test00_getReceipt_withValidPaymentId_returnsPdf() throws Exception {
        Payment payment = buildPayment(new BigDecimal("1000.00"));

        mockMvc.perform(get("/api/v1/payments/" + payment.getId() + "/receipt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("recibo-" + payment.getId() + ".pdf")))
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    assert body.length > 0 : "PDF body should not be empty";
                });
    }

    @Test
    void test01_getReceipt_withNonExistentPaymentId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/payments/99999/receipt"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/v1/payments/export ────────────────────────────────────────────

    @Test
    void test02_exportPayments_withExistingPayments_returnsExcel() throws Exception {
        buildPayment(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/v1/payments/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("pagos.xlsx")))
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    assert body.length > 0 : "Excel body should not be empty";
                });
    }

    @Test
    void test03_exportPayments_withNoPayments_returnsEmptyExcel() throws Exception {
        mockMvc.perform(get("/api/v1/payments/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    assert body.length > 0 : "Excel with headers should not be empty";
                });
    }
}
