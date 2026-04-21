package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;
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

    @Transactional(readOnly = true)
    public List<PropertyResponse> getByFilters(String building, String status) {
        Property.PaymentStatus paymentStatus = parsePaymentStatus(status);
        String bFilter = (building != null && !building.trim().isEmpty()) ? building : null;

        return propertyRepository.findByFilters(bFilter, paymentStatus).stream().map(this::toDto).collect(Collectors.toList());
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

    private Property.PaymentStatus parsePaymentStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            if (status.equalsIgnoreCase("PAID")) return Property.PaymentStatus.PAID;
            else if (status.equalsIgnoreCase("PENDING")) return Property.PaymentStatus.PENDING;
            else if (status.equalsIgnoreCase("OVERDUE")) return Property.PaymentStatus.OVERDUE;
            else return Property.PaymentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    private void validatePropertyUniqueness(PropertyRequest request) {
        if (propertyRepository.existsByAddressAndBuildingAndFloor(request.getAddress(), request.getBuilding(), request.getFloor())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Property already exists with that address, building and floor");
        }
    }

    private Property fromDto(PropertyRequest request) {
        Property property = new Property();
        property.setBuilding(request.getBuilding());
        property.setFloor(request.getFloor());
        property.setArea(request.getArea());
        property.setRooms(request.getRooms());
        property.setAddress(request.getAddress());
        property.setUnitType(request.getUnitType());
        property.setExpenses(request.getExpenses());
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        property.setPaymentStatus(Property.PaymentStatus.PAID);
        return property;
    }
}
