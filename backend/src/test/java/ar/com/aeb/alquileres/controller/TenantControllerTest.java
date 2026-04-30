package ar.com.aeb.alquileres.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.TenantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@DisplayName("TenantController Tests")
class TenantControllerTest extends BaseControllerTest {

    @Autowired
    private TenantRepository tenantRepository;

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
}
