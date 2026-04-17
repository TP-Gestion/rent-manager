package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Create a new property
     */
    public PropertyResponse create(PropertyRequest request) {
        Property property = new Property(
                request.getAddress(), request.getCity(), request.getProvince(), request.getBedrooms(), request.getBathrooms(), request.getRentalPrice()
        );
        property.setPostalCode(request.getPostalCode());
        property.setDescription(request.getDescription());

        Property saved = propertyRepository.save(property);
        return new PropertyResponse(saved);
    }

    /**
     * Get property by ID
     */
    public PropertyResponse getById(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        return new PropertyResponse(property);
    }

    /**
     * Get all properties
     */
    @Transactional(readOnly = true)
    public List<PropertyResponse> getAll() {
        return propertyRepository.findAll().stream().map(PropertyResponse::new).collect(Collectors.toList());
    }

    /**
     * Get properties by city
     */
    @Transactional(readOnly = true)
    public List<PropertyResponse> getByCity(String city) {
        return propertyRepository.findByCity(city).stream().map(PropertyResponse::new).collect(Collectors.toList());
    }

    /**
     * Get properties by status
     */
    @Transactional(readOnly = true)
    public List<PropertyResponse> getByStatus(String status) {
        Property.PropertyStatus propertyStatus = Property.PropertyStatus.valueOf(status);
        return propertyRepository.findByStatus(propertyStatus).stream().map(PropertyResponse::new).collect(Collectors.toList());
    }

    /**
     * Get available properties
     */
    @Transactional(readOnly = true)
    public List<PropertyResponse> getAvailable() {
        return propertyRepository.findByStatusOrderByCreatedAtDesc(Property.PropertyStatus.AVAILABLE).stream().map(PropertyResponse::new).collect(Collectors.toList());
    }

    /**
     * Update property
     */
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setProvince(request.getProvince());
        property.setPostalCode(request.getPostalCode());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setRentalPrice(request.getRentalPrice());
        property.setDescription(request.getDescription());

        Property updated = propertyRepository.save(property);
        return new PropertyResponse(updated);
    }

    /**
     * Update property status
     */
    public PropertyResponse updateStatus(Long id, String status) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        Property.PropertyStatus propertyStatus = Property.PropertyStatus.valueOf(status);
        property.setStatus(propertyStatus);

        Property updated = propertyRepository.save(property);
        return new PropertyResponse(updated);
    }

    /**
     * Delete property
     */
    public void delete(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    /**
     * Count total properties
     */
    @Transactional(readOnly = true)
    public long count() {
        return propertyRepository.count();
    }
}
