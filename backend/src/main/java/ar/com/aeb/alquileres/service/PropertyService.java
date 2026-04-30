package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.property.PropertyDetailsResponse;
import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.dto.property.PropertySummaryResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.property.DuplicatePropertyException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.*;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<PropertySummaryResponse> getSummary() {
        return propertyRepository.findAll().stream().map(this::buildSummary).collect(Collectors.toList());
    }

    private PropertySummaryResponse buildSummary(Property property) {
        PropertySummaryResponse summary = new PropertySummaryResponse();
        summary.setId(property.getId());
        summary.setEdificio(property.getBuilding().getName());
        summary.setPiso(property.getFloor());
        summary.setTipoUnidad(property.getUnitType());
        summary.setEstadoOcupacion(property.getOccupancyStatus().name());

        if (property.getTenant() != null) {
            summary.setInquilino(new PropertySummaryResponse.TenantSummary(property.getTenant().getId(), property.getTenant().getFirstName(), property.getTenant().getLastName()));
        }

        // Get contracts and expenses to calculate total and status
        List<RentalContract> contracts = rentalContractRepository.findByPropertyId(property.getId());
        List<PropertyExpense> expenses = property.getPropertyExpenses();

        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDate earliestDueDate = null;
        boolean hasPending = false;
        boolean hasOverdue = false;

        // Process Contracts
        for (RentalContract contract : contracts) {
            if (contract.getStatus() != RentalContract.RentalContractStatus.PAID) {
                hasPending = true;
                totalAmount = totalAmount.add(contract.getAmount());
                if (contract.getStatus() == RentalContract.RentalContractStatus.OVERDUE) {
                    hasOverdue = true;
                }
                if (earliestDueDate == null || contract.getDueDate().isBefore(earliestDueDate)) {
                    earliestDueDate = contract.getDueDate();
                }
            }
        }

        // Process Expenses
        for (PropertyExpense pe : expenses) {
            if (pe.getStatus() != PropertyExpense.PropertyExpenseStatus.PAID) {
                hasPending = true;
                totalAmount = totalAmount.add(pe.getAmount());
                if (pe.getStatus() == PropertyExpense.PropertyExpenseStatus.OVERDUE) {
                    hasOverdue = true;
                }
            }
        }

        summary.setMontoTotal(totalAmount);
        summary.setFechaVencimiento(earliestDueDate);

        if (hasOverdue) {
            summary.setEstadoPago("OVERDUE");
        } else if (hasPending) {
            summary.setEstadoPago("PENDING");
        } else {
            summary.setEstadoPago("PAID");
        }

        return summary;
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
