package ar.com.aeb.alquileres.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.TenantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@DisplayName("TenantController Tests")
class TenantControllerTest extends BaseControllerTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Test
    void test00_createTenant_withValidData_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"jdoe@example.com\",\"phone\":\"1123456789\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value(201)).andExpect(jsonPath("$.message").value("Tenant created successfully")).andExpect(jsonPath("$.data.firstName").value("Juan")).andExpect(jsonPath("$.data.email").value("jdoe@example.com"));
    }

    @Test
    void test01_createTenant_withInvalidEmailFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"usuariosindominio\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test02_createTenant_withPhoneLessThanTenDigits_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"123456789\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void test03_createTenant_withPhoneMoreThanTenDigits_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"11234567890\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void test04_createTenant_withDuplicateEmail_returnsConflict() throws Exception {
        // Pre-insert a tenant
        tenantRepository.save(new Tenant("Ana", "Perez", "ana@example.com", "1100000001"));

        // Attempt to insert with SAME email but DIFFERENT phone
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Marcelo\",\"lastName\":\"Gómez\",\"email\":\"ana@example.com\",\"phone\":\"1199999999\"}")).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void test05_createTenant_withDuplicatePhone_returnsConflict() throws Exception {
        tenantRepository.save(new Tenant("Karina", "Rios", "kara@example.com", "1166666666"));

        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Hugo\",\"lastName\":\"Sosa\",\"email\":\"hugo@example.com\",\"phone\":\"1166666666\"}")).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void test06_getTenantDetail_withValidId_returnsOk() throws Exception {
        Tenant saved = tenantRepository.save(new Tenant("Pedro", "García", "pedro@example.com", "1188888888"));

        mockMvc.perform(get("/api/v1/tenants/" + saved.getId() + "/detail")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data.firstName").value("Pedro"));
    }

    @Test
    void test07_getTenantDetail_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/99999/detail")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void test08_createTenant_withMissingFirstName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void test09_createTenant_withMissingEmail_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void test10_createTenant_withMissingPhone_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void test11_deactivateTenant_marksInactiveAndKeepsRow() throws Exception {
        Tenant saved = tenantRepository.save(new Tenant("Laura", "Diaz", "laura@example.com", "1144444444"));

        mockMvc.perform(delete("/api/v1/tenants/" + saved.getId())).andExpect(status().isNoContent());

        Tenant reloaded = tenantRepository.findById(saved.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertFalse(reloaded.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(reloaded.getDeactivatedAt());
    }

    @Test
    void test12_deactivateTenant_freesAssignedProperty() throws Exception {
        Building building = buildingRepository.save(new Building("Edificio Baja", "Calle Falsa 123"));
        Tenant tenant = tenantRepository.save(new Tenant("Mario", "Lopez", "mario@example.com", "1155555555"));

        Property property = new Property();
        property.setBuilding(building);
        property.setFloor("2B");
        property.setArea(60.0);
        property.setRooms(3);
        property.setUnitType("Apartment");
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        property.setTenant(tenant);
        property = propertyRepository.save(property);

        mockMvc.perform(delete("/api/v1/tenants/" + tenant.getId())).andExpect(status().isNoContent());

        Property reloaded = propertyRepository.findById(property.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertNull(reloaded.getTenant());
        org.junit.jupiter.api.Assertions.assertEquals(Property.OccupancyStatus.AVAILABLE, reloaded.getOccupancyStatus());
    }

    @Test
    void test13_deactivateAlreadyInactiveTenant_returnsConflict() throws Exception {
        Tenant tenant = new Tenant("Sofia", "Ruiz", "sofia@example.com", "1166777888");
        tenant.setActive(false);
        Tenant saved = tenantRepository.save(tenant);

        mockMvc.perform(delete("/api/v1/tenants/" + saved.getId())).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void test14_getAllTenants_excludesInactiveByDefault() throws Exception {
        Tenant active = tenantRepository.save(new Tenant("Activo", "Uno", "activo@example.com", "1100000010"));
        Tenant inactive = new Tenant("Inactivo", "Dos", "inactivo@example.com", "1100000011");
        inactive.setActive(false);
        tenantRepository.save(inactive);

        mockMvc.perform(get("/api/v1/tenants")).andExpect(status().isOk()).andExpect(jsonPath("$.data[?(@.email=='activo@example.com')]").exists()).andExpect(jsonPath("$.data[?(@.email=='inactivo@example.com')]").doesNotExist());
    }

    @Test
    void test15_getAllTenants_includeInactive_returnsInactive() throws Exception {
        Tenant inactive = new Tenant("Inactivo", "Tres", "inactivo3@example.com", "1100000012");
        inactive.setActive(false);
        tenantRepository.save(inactive);

        mockMvc.perform(get("/api/v1/tenants").param("includeInactive", "true")).andExpect(status().isOk()).andExpect(jsonPath("$.data[?(@.email=='inactivo3@example.com')]").exists());
    }
}
