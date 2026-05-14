package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.property.PropertyDetailsResponse;
import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.dto.property.PropertySummaryResponse;
import ar.com.aeb.alquileres.dto.paymentDetails.PaymentDetailsResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.service.PropertyService;
import ar.com.aeb.alquileres.service.ExpenseService;
import ar.com.aeb.alquileres.service.RentalContractService;
import ar.com.aeb.alquileres.service.PaymentDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    /**
     * CREATE - Add a new property
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(@Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Property created successfully", response));
    }

    /**
     * READ - Get all properties
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getProperties() {
        List<PropertyResponse> properties = propertyService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Success", properties));
    }

    /**
     * READ - Get properties summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<PropertySummaryResponse>>> getPropertiesSummary() {
        List<PropertySummaryResponse> summary = propertyService.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Success", summary));
    }

    /**
     * READ - Get payment details for a property
     */
    @GetMapping("/{id}/payment-details")
    public ResponseEntity<ApiResponse<PaymentDetailsResponse>> getPropertyPaymentDetails(@PathVariable Long id) {
        PaymentDetailsResponse paymentDetails = paymentDetailsService.getPaymentDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Success", paymentDetails));
    }


    /**
     * READ - Get property by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyById(@PathVariable Long id) {
        PropertyResponse property = propertyService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", property));
    }

    /**
     * READ - Get property details by ID
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<PropertyDetailsResponse>> getPropertyDetails(@PathVariable Long id) {
        PropertyDetailsResponse details = propertyService.getDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Success", details));
    }

    /**
     * CREATE - Add a new tenant to a property
     */
    @PostMapping("/{id}/tenants")
    public ResponseEntity<ApiResponse<PropertyResponse>> createPropertyTenant(@PathVariable Long id, @Valid @RequestBody TenantRequest request) {
        PropertyResponse response = propertyService.createAndAssignTenant(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Tenant created and assigned successfully", response));
    }

    /**
     * UPDATE - Assign a tenant to a property
     */
    @PatchMapping("/{id}/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<PropertyResponse>> assignTenant(@PathVariable Long id, @PathVariable Long tenantId) {
        PropertyResponse response = propertyService.assignTenant(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Tenant assigned successfully", response));
    }

    /**
     * UPDATE - Remove a tenant from a property
     */
    @DeleteMapping("/{id}/tenant")
    public ResponseEntity<ApiResponse<PropertyResponse>> removeTenant(@PathVariable Long id) {
        PropertyResponse response = propertyService.removeTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant removed successfully", response));
    }

    /**
     * UPDATE - Update a property
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Property updated successfully", response));
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
    public ResponseEntity<ApiResponse<Long>> countProperties() {
        long count = propertyService.count();
        return ResponseEntity.ok(ApiResponse.success("Success", count));
    }

    /**
     * READ - Get expenses for a property
     */
    @GetMapping("/{propertyId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getPropertyExpenses(@PathVariable Long propertyId) {
        List<ExpenseResponse> response = expenseService.getExpenses(propertyId, null);
        return ResponseEntity.ok(ApiResponse.success("Success", response));
    }

    /**
     * CREATE - Add a new expense to a property
     */
    @PostMapping("/{propertyId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> createPropertyExpense(@PathVariable Long propertyId, @Valid @RequestBody ExpenseRequest request) {
        List<ExpenseResponse> response = expenseService.createPropertyExpense(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Expense created successfully", response));
    }

    /**
     * READ - Get rental contracts for a property
     */
    @GetMapping("/{propertyId}/rental-contract")
    public ResponseEntity<ApiResponse<List<RentalContractResponse>>> getPropertyRentalContracts(@PathVariable Long propertyId) {
        List<RentalContractResponse> response = rentalContractService.getByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Success", response));
    }

    /**
     * CREATE - Add a new rental contract to a property
     */
    @PostMapping(value = "/{propertyId}/rental-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RentalContractResponse>> createPropertyRentalContract(
            @PathVariable Long propertyId,
            @Valid @ModelAttribute RentalContractRequest request) {
        RentalContractResponse response = rentalContractService.create(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Rental contract created successfully", response));
    }

    /**
     * READ - Get contract file
     */
    @GetMapping("/{propertyId}/rental-contract/{contractId}/file")
    public ResponseEntity<Resource> getContractFile(@PathVariable Long propertyId, @PathVariable Long contractId) {
        Resource resource = rentalContractService.getContractResource(contractId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
