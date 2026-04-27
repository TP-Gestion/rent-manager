package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.service.RentalContractService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rental-contracts")
public class RentalContractController {

    @Autowired
    private RentalContractService rentalContractService;

    /**
     * Create a new rental contract
     */
    @PostMapping
    public ResponseEntity<?> createContract(@Valid @RequestBody RentalContractRequest request) {
        RentalContractResponse response = rentalContractService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "Rental contract created successfully", response));
    }

    /**
     * Get all contracts
     */
    @GetMapping
    public ResponseEntity<List<RentalContractResponse>> getAllContracts() {
        List<RentalContractResponse> contracts = rentalContractService.getAll();
        return ResponseEntity.ok(contracts);
    }

    /**
     * Get contract by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getContractDetail(@PathVariable Long id) {
        RentalContractResponse contract = rentalContractService.getDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", contract));
    }

    /**
     * Get contracts by tenant ID
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentalContractResponse>> getContractsByTenant(@PathVariable Long tenantId) {
        List<RentalContractResponse> contracts = rentalContractService.getByTenant(tenantId);
        return ResponseEntity.ok(contracts);
    }

    /**
     * Get contracts by property ID
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<RentalContractResponse>> getContractsByProperty(@PathVariable Long propertyId) {
        List<RentalContractResponse> contracts = rentalContractService.getByProperty(propertyId);
        return ResponseEntity.ok(contracts);
    }

    /**
     * Update a rental contract
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContract(@PathVariable Long id, @Valid @RequestBody RentalContractRequest request) {
        RentalContractResponse response = rentalContractService.update(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Rental contract updated successfully", response));
    }

    /**
     * Update contract status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateContractStatus(@PathVariable Long id, @RequestParam RentalContract.ContractStatus status) {
        RentalContractResponse response = rentalContractService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Contract status updated successfully", response));
    }

    /**
     * Delete a rental contract
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {
        rentalContractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
