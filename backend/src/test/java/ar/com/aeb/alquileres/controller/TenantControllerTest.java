package ar.com.aeb.alquileres.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.exception.DuplicateEmailException;
import ar.com.aeb.alquileres.exception.DuplicatePhoneException;
import ar.com.aeb.alquileres.exception.TenantNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("TenantController Tests")
class TenantControllerTest extends BaseControllerTest {

    @Test
    void test00_createTenant_withValidData_returnsCreated() throws Exception {
        TenantResponse response = new TenantResponse();
        response.setId(1L);
        response.setFirstName("Juan");
        response.setLastName("García");
        response.setEmail("juan@example.com");
        response.setPhone("1123456789");
        response.setCreatedAt(LocalDateTime.now());

        when(tenantService.create(any(TenantRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"1123456789\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value(201)).andExpect(jsonPath("$.message").value("Tenant created successfully")).andExpect(jsonPath("$.data.firstName").value("Juan")).andExpect(jsonPath("$.data.email").value("juan@example.com"));
    }

    @Test
    void test01_createTenant_withInvalidEmailFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"usuariosindominio\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    void test02_createTenant_withPhoneLessThanTenDigits_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"123456789\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value(containsString("Phone should be 10 digits")));
    }

    @Test
    void test03_createTenant_withPhoneMoreThanTenDigits_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"11234567890\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value(containsString("Phone should be 10 digits")));
    }

    @Test
    void test04_createTenant_withDuplicateEmail_returnsConflict() throws Exception {
        when(tenantService.create(any(TenantRequest.class)))
                .thenThrow(new DuplicateEmailException("juan@example.com"));

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"1123456789\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("Email already exists")));
    }

    @Test
    void test05_createTenant_withDuplicatePhone_returnsConflict() throws Exception {
        when(tenantService.create(any(TenantRequest.class)))
                .thenThrow(new DuplicatePhoneException("1123456789"));

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"1123456789\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("Phone number already exists")));
    }

    @Test
    void test06_getTenantDetail_withValidId_returnsOk() throws Exception {
        TenantResponse response = new TenantResponse();
        response.setId(1L);
        response.setFirstName("Juan");
        response.setLastName("García");
        response.setEmail("juan@example.com");
        response.setPhone("1123456789");

        when(tenantService.getDetail(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tenants/1/detail")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data.firstName").value("Juan"));
    }

    @Test
    void test07_getTenantDetail_withInvalidId_returnsNotFound() throws Exception {
        when(tenantService.getDetail(999L))
                .thenThrow(new TenantNotFoundException(999L));

        mockMvc.perform(get("/api/v1/tenants/999/detail"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void test08_createTenant_withMissingFirstName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"lastName\":\"García\",\"email\":\"juan@example.com\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test09_createTenant_withMissingEmail_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"phone\":\"1123456789\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void test10_createTenant_withMissingPhone_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Juan\",\"lastName\":\"García\",\"email\":\"juan@example.com\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
}
