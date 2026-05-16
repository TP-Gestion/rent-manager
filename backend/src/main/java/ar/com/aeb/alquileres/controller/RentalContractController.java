package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.service.RentalContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rental-contracts")
public class RentalContractController {

    @Autowired
    private RentalContractService rentalContractService;

    /**
     * Get contract by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalContractResponse>> getContractDetail(@PathVariable Long id) {
        RentalContractResponse contract = rentalContractService.getDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Success", contract));
    }

    /**
     * Update a rental contract
     */
    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RentalContractResponse>> updateContract(@PathVariable Long id, @Valid @ModelAttribute RentalContractRequest request) {
        RentalContractResponse response = rentalContractService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Rental contract updated successfully", response));
    }

    /**
     * Update contract status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RentalContractResponse>> updateContractStatus(@PathVariable Long id, @RequestParam RentalContract.RentalContractStatus status) {
        RentalContractResponse response = rentalContractService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Contract status updated successfully", response));
    }

    /**
     * Delete a rental contract
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        rentalContractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
