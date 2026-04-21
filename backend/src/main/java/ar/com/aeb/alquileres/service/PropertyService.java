package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TenantService tenantService;

    public PropertyResponse create(PropertyRequest request) {
        validatePropertyUniqueness(request);
        Property property = fromDto(request);
        return toDto(propertyRepository.save(property));
    }

    @Transactional(readOnly = true)
    public PropertyResponse getById(Long id) {
        return toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getAll() {
        return propertyRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        findById(id);
        propertyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return propertyRepository.count();
    }

    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found: " + id));
    }

    public PropertyResponse toDto(Property property) {
        return new PropertyResponse(property);
    }

    private Property.OccupancyStatus parseOccupancyStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return Property.OccupancyStatus.AVAILABLE;
        }
        try {
            return Property.OccupancyStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return Property.OccupancyStatus.AVAILABLE;
        }
    }

    private void validatePropertyUniqueness(PropertyRequest request) {
        // Validation can be extended if needed
    }

    private Property fromDto(PropertyRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        
        Property property = new Property();
        property.setBuilding(building);
        property.setFloor(request.getFloor());
        property.setArea(request.getArea());
        property.setRooms(request.getRooms());
        property.setUnitType(request.getUnitType());
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        return property;
    }
}
