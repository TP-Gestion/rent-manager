package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.property.PropertyDetailsResponse;
import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.service.PropertyService;
import ar.com.aeb.alquileres.service.ExpenseService;
import ar.com.aeb.alquileres.service.RentalContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private RentalContractService rentalContractService;

    /**
     * CREATE - Add a new property
     */
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * READ - Get all properties
     */
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getProperties() {
        List<PropertyResponse> properties = propertyService.getAll();
        return ResponseEntity.ok(properties);
    }

    /**
     * READ - Get property by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        PropertyResponse property = propertyService.getById(id);
        return ResponseEntity.ok(property);
    }

    /**
     * READ - Get property details by ID
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<PropertyDetailsResponse> getPropertyDetails(@PathVariable Long id) {
        PropertyDetailsResponse details = propertyService.getDetails(id);
        return ResponseEntity.ok(details);
    }

    /**
     * CREATE - Add a new tenant to a property
     */
    @PostMapping("/{id}/tenants")
    public ResponseEntity<PropertyResponse> createPropertyTenant(@PathVariable Long id, @Valid @RequestBody TenantRequest request) {
        PropertyResponse response = propertyService.createAndAssignTenant(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UPDATE - Assign a tenant to a property
     */
    @PatchMapping("/{id}/tenant/{tenantId}")
    public ResponseEntity<PropertyResponse> assignTenant(@PathVariable Long id, @PathVariable Long tenantId) {
        PropertyResponse response = propertyService.assignTenant(id, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE - Remove a tenant from a property
     */
    @DeleteMapping("/{id}/tenant")
    public ResponseEntity<PropertyResponse> removeTenant(@PathVariable Long id) {
        PropertyResponse response = propertyService.removeTenant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE - Update a property
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - Remove a property
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * READ - Get total count of properties
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProperties() {
        long count = propertyService.count();
        return ResponseEntity.ok(count);
    }

    /**
     * READ - Get expenses for a property
     */
    @GetMapping("/{propertyId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getPropertyExpenses(@PathVariable Long propertyId) {
        List<ExpenseResponse> response = expenseService.getExpenses(propertyId, null);
        return ResponseEntity.ok(response);
    }

    /**
     * CREATE - Add a new expense to a property
     */
    @PostMapping("/{propertyId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> createPropertyExpense(@PathVariable Long propertyId, @Valid @RequestBody ExpenseRequest request) {
        List<ExpenseResponse> response = expenseService.createPropertyExpense(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * READ - Get rental contracts for a property
     */
    @GetMapping("/{propertyId}/rental-contract")
    public ResponseEntity<List<RentalContractResponse>> getPropertyRentalContracts(@PathVariable Long propertyId) {
        List<RentalContractResponse> response = rentalContractService.getByProperty(propertyId);
        return ResponseEntity.ok(response);
    }

    /**
     * CREATE - Add a new rental contract to a property
     */
    @PostMapping("/{propertyId}/rental-contract")
    public ResponseEntity<RentalContractResponse> createPropertyRentalContract(@PathVariable Long propertyId, @Valid @RequestBody RentalContractRequest request) {
        RentalContractResponse response = rentalContractService.create(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
