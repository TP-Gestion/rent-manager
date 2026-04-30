package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.property.PropertyDetailsResponse;
import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.property.DuplicatePropertyException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
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

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RentalContractRepository rentalContractRepository;

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
    public PropertyDetailsResponse getDetails(Long id) {
        Property property = findById(id);
        List<RentalContract> contracts = rentalContractRepository.findByPropertyId(id);
        RentalContract activeContract = contracts.stream().filter(c -> c.getStatus() != RentalContract.RentalContractStatus.PAID).findFirst().orElse(null);
        return new PropertyDetailsResponse(property, activeContract);
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getAll() {
        return propertyRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        findById(id);
        propertyRepository.deleteById(id);
    }

    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = findById(id);
        Building building = buildingRepository.findById(request.getBuildingId()).orElseThrow(() -> new BuildingNotFoundException(request.getBuildingId()));

        if (!property.getBuilding().getId().equals(request.getBuildingId()) || !property.getFloor().equals(request.getFloor())) {

            if (propertyRepository.findByBuildingAndFloor(building, request.getFloor()).isPresent()) {
                throw new DuplicatePropertyException("A property already exists in building '" + building.getName() + "' on floor '" + request.getFloor() + "'");
            }
        }

        property.setBuilding(building);
        property.setFloor(request.getFloor());
        property.setArea(request.getArea());
        property.setRooms(request.getRooms());
        property.setUnitType(request.getUnitType());

        return toDto(propertyRepository.save(property));
    }

    @Transactional(readOnly = true)
    public long count() {
        return propertyRepository.count();
    }

    public PropertyResponse assignTenant(Long propertyId, Long tenantId) {
        Property property = findById(propertyId);
        property.setTenant(tenantService.findById(tenantId));
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        return toDto(propertyRepository.save(property));
    }

    public PropertyResponse createAndAssignTenant(Long propertyId, TenantRequest request) {
        Property property = findById(propertyId);
        Tenant tenant = tenantService.createEntity(request);
        property.setTenant(tenant);
        property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        return toDto(propertyRepository.save(property));
    }

    public PropertyResponse removeTenant(Long propertyId) {
        Property property = findById(propertyId);
        property.setTenant(null);
        property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        return toDto(propertyRepository.save(property));
    }

    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException(id));
    }

    public PropertyResponse toDto(Property property) {
        return new PropertyResponse(property);
    }

    private void validatePropertyUniqueness(PropertyRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId()).orElseThrow(() -> new BuildingNotFoundException(request.getBuildingId()));

        if (propertyRepository.findByBuildingAndFloor(building, request.getFloor()).isPresent()) {
            throw new DuplicatePropertyException("A property already exists in building '" + building.getName() + "' on floor '" + request.getFloor() + "'");
        }
    }

    private Property fromDto(PropertyRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId()).orElseThrow(() -> new BuildingNotFoundException(request.getBuildingId()));

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
