package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantController {

    @Autowired
    private TenantService tenantService;

    /**
     * CREATE - Add a new tenant
     */
    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody TenantRequest request) {
        TenantResponse response = tenantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * READ - Get all tenants
     */
    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantService.getAll();
        return ResponseEntity.ok(tenants);
    }

    /**
     * READ - Get tenant detail by ID
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<TenantResponse> getTenantDetail(@PathVariable Long id) {
        TenantResponse tenant = tenantService.getDetail(id);
        return ResponseEntity.ok(tenant);
    }

    /**
     * UPDATE - Update a tenant
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable Long id,
            @Valid @RequestBody TenantRequest request) {
        TenantResponse response = tenantService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - Remove a tenant
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
