package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.service.TenantService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    public ResponseEntity<?> createTenant(@Valid @RequestBody TenantRequest request) {
        TenantResponse response = tenantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tenant created successfully", response));
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantService.getAll();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getTenantDetail(@PathVariable Long id) {
        TenantResponse tenant = tenantService.getDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", tenant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenant(
                                          @PathVariable Long id, @Valid @RequestBody TenantRequest request) {
        TenantResponse response = tenantService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tenant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
