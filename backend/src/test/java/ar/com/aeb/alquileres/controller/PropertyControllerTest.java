package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PropertyController Tests")
class PropertyControllerTest extends BaseControllerTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Test
    void testGetPropertyDetails_withValidId_returnsOk() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building", "Test Address"));

        Tenant tenant = tenantRepository.save(new Tenant("Juan", "Perez", "juan@example.com", "1122334455"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("1A");
        property.setArea(50.0);
        property.setRooms(2);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        property = propertyRepository.save(property);

        RentalContract contract = new RentalContract(property, new BigDecimal("1000.00"), LocalDate.now().plusDays(5));
        contract.setStatus(RentalContract.RentalContractStatus.PENDING);
        rentalContractRepository.save(contract);

        // Execute & Verify
        mockMvc.perform(get("/api/v1/properties/" + property.getId() + "/details")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(property.getId())).andExpect(jsonPath("$.building.name").value("Test Building")).andExpect(jsonPath("$.tenant.firstName").value("Juan")).andExpect(jsonPath("$.activeContract.amount").value(1000.00)).andExpect(jsonPath("$.occupancyStatus").value("OCCUPIED"));
    }

    @Test
    void testAssignTenant_withValidIds_returnsOk() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building", "Test Address"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("2B");
        property.setArea(45.0);
        property.setRooms(1);
        property.setUnitType("Studio");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        Tenant tenant = tenantRepository.save(new Tenant("Maria", "Lopez", "maria@example.com", "1133445566"));

        // Execute & Verify
        mockMvc.perform(patch("/api/v1/properties/" + property.getId() + "/tenant/" + tenant.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.occupancyStatus").value("OCCUPIED"));

        // Verify in DB
        Property updated = propertyRepository.findById(property.getId()).orElseThrow();
        assert updated.getTenant().getId().equals(tenant.getId());
        assert updated.getOccupancyStatus() == Property.OccupancyStatus.OCCUPIED;
    }

    @Test
    void testCreatePropertyTenant_withValidData_returnsCreated() throws Exception {
        // Setup
        Building building = buildingRepository.save(new Building("Test Building 2", "Address 2"));
        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("3C");
        property.setArea(60.0);
        property.setRooms(3);
        property.setUnitType("Flat");
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        String tenantJson = "{\"firstName\":\"Carlos\",\"lastName\":\"Perez\",\"email\":\"carlos@example.com\",\"phone\":\"1122223333\"}";

        // Execute & Verify
        mockMvc.perform(post("/api/v1/properties/" + property.getId() + "/tenants").contentType(MediaType.APPLICATION_JSON).content(tenantJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.occupancyStatus").value("OCCUPIED"));

        // Verify in DB
        Property updated = propertyRepository.findById(property.getId()).orElseThrow();
        assert updated.getTenant() != null;
        assert updated.getTenant().getFirstName().equals("Carlos");
        assert updated.getOccupancyStatus() == Property.OccupancyStatus.OCCUPIED;
    }
}
