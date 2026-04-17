package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

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
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
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
     * READ - Get properties by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByCity(@PathVariable String city) {
        List<PropertyResponse> properties = propertyService.getByCity(city);
        return ResponseEntity.ok(properties);
    }

    /**
     * READ - Get properties by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByStatus(@PathVariable String status) {
        List<PropertyResponse> properties = propertyService.getByStatus(status);
        return ResponseEntity.ok(properties);
    }

    /**
     * READ - Get available properties
     */
    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties() {
        List<PropertyResponse> properties = propertyService.getAvailable();
        return ResponseEntity.ok(properties);
    }

    /**
     * UPDATE - Update a property
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
                                                           @PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE - Update property status only
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PropertyResponse> updatePropertyStatus(
                                                                 @PathVariable Long id, @RequestParam String status) {
        PropertyResponse response = propertyService.updateStatus(id, status);
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
}
