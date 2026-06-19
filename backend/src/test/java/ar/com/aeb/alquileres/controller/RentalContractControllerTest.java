package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import ar.com.aeb.alquileres.service.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RentalContractController Tests")
class RentalContractControllerTest extends BaseControllerTest {

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("Update Rental Contract with PDF")
    void testUpdateContract_withPdf_returnsOk() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building", "Test Address"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("1A");
        property.setArea(50.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, new BigDecimal("1000.00"), LocalDate.now().plusMonths(1));
        contract = rentalContractRepository.save(contract);

        MockMultipartFile pdfFile = new MockMultipartFile(
                "contract", "test.pdf", "application/pdf", "test content".getBytes()
        );

        Mockito.when(fileStorageService.storeFile(any(), eq(pdfFile), any(), any())).thenReturn("2026/05/test_mock.pdf");

        // Execute & Verify
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/rental-contracts/" + contract.getId()).file(pdfFile).param("amount", "1500.00").param("dueDate", LocalDate.now().plusMonths(2).toString()).with(request -> {
            request.setMethod("PUT");
            return request;
        })).andExpect(status().isOk()).andExpect(jsonPath("$.data.amount").value(1500.00)).andExpect(jsonPath("$.data.hasContract").value(true));

        // Verify in DB
        RentalContract updated = rentalContractRepository.findById(contract.getId()).orElseThrow();
        assert updated.getAmount().compareTo(new BigDecimal("1500.00")) == 0;
        assert updated.getContractPath().equals("2026/05/test_mock.pdf");

        // Verify deleteFile was called for the old path (if it had one, in this case it didn't)
        Mockito.verify(fileStorageService, Mockito.atLeastOnce()).storeFile(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Partial Update Rental Contract")
    void testUpdateContract_partialUpdate_returnsOk() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building 2", "Address 2"));
        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("2B");
        property.setArea(60.0);
        property.setRooms(3);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        LocalDate originalDate = LocalDate.now().plusMonths(1);
        RentalContract contract = new RentalContract(property, new BigDecimal("2000.00"), originalDate);
        contract = rentalContractRepository.save(contract);

        // Execute & Verify - Only update amount
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/rental-contracts/" + contract.getId()).param("amount", "2500.00").with(request -> {
            request.setMethod("PUT");
            return request;
        })).andExpect(status().isOk()).andExpect(jsonPath("$.data.amount").value(2500.00)).andExpect(jsonPath("$.data.dueDate").value(originalDate.toString()));

        // Verify in DB
        RentalContract updated = rentalContractRepository.findById(contract.getId()).orElseThrow();
        assert updated.getAmount().compareTo(new BigDecimal("2500.00")) == 0;
        assert updated.getDueDate().equals(originalDate);
    }

    @Test
    @DisplayName("Delete Rental Contract and File")
    void testDeleteContract_callsFileDelete_returnsNoContent() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building 3", "Address 3"));
        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("3C");
        property.setArea(50.0);
        property.setRooms(1);
        property.setUnitType("Office");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, new BigDecimal("3000.00"), LocalDate.now().plusMonths(3));
        contract.setContractPath("path/to/delete.pdf");
        contract = rentalContractRepository.save(contract);

        // Execute
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/rental-contracts/" + contract.getId())).andExpect(status().isNoContent());

        // Verify
        Mockito.verify(fileStorageService).deleteFile(any(), eq("path/to/delete.pdf"));
        assert !rentalContractRepository.existsById(contract.getId());
    }
}
