package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.exception.tenant.DuplicateEmailException;
import ar.com.aeb.alquileres.exception.tenant.DuplicatePhoneException;
import ar.com.aeb.alquileres.exception.tenant.TenantAlreadyInactiveException;
import ar.com.aeb.alquileres.exception.tenant.TenantNotFoundException;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.TenantRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    /**
     * Create a new tenant entity
     */
    public Tenant createEntity(TenantRequest request) {
        // Validate if email already exists
        if (tenantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Validate if phone already exists
        if (tenantRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new DuplicatePhoneException(request.getPhone());
        }

        Tenant tenant = new Tenant(
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone()
        );

        return tenantRepository.save(tenant);
    }

    /**
     * Create a new tenant
     */
    public TenantResponse create(TenantRequest request) {
        Tenant saved = createEntity(request);
        return new TenantResponse(saved);
    }

    /**
     * Get tenant entity by ID
     */
    public Tenant findById(Long id) {
        return tenantRepository.findById(id).orElseThrow(() -> new TenantNotFoundException(id));
    }

    /**
     * Get tenant by ID - Detail endpoint
     */
    @Transactional(readOnly = true)
    public TenantResponse getDetail(Long id) {
        Tenant tenant = findById(id);
        return new TenantResponse(tenant);
    }

    /**
     * Get all tenants
     */
    @Transactional(readOnly = true)
    public List<TenantResponse> getAll(boolean includeInactive) {
        List<Tenant> tenants = includeInactive ? tenantRepository.findAll() : tenantRepository.findByActiveTrue();
        return tenants.stream().map(TenantResponse::new).collect(Collectors.toList());
    }

    /**
     * Update tenant
     */
    public TenantResponse update(Long id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow(() -> new TenantNotFoundException(id));

        tenant.setFirstName(request.getFirstName());
        tenant.setLastName(request.getLastName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());

        Tenant updated = tenantRepository.save(tenant);
        return new TenantResponse(updated);
    }

    /**
     * Get all buildings where the tenant has at least one property
     */
    @Transactional(readOnly = true)
    public List<BuildingResponse> getBuildings(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new TenantNotFoundException(id);
        }
        return buildingRepository.findDistinctByTenantId(id).stream()
                .map(BuildingResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Deactivate (soft delete) a tenant, keeping the row so payment history stays intact.
     * Any property still assigned to the tenant is freed (set to AVAILABLE).
     */
    public void delete(Long id) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow(() -> new TenantNotFoundException(id));

        if (!tenant.isActive()) {
            throw new TenantAlreadyInactiveException(id);
        }

        // Free any property currently occupied by this tenant
        for (Property property : propertyRepository.findByTenantId(id)) {
            property.setTenant(null);
            property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
            propertyRepository.save(property);
        }

        tenant.setActive(false);
        tenant.setDeactivatedAt(LocalDate.now());
        tenantRepository.save(tenant);
    }

    public Tenant fromDto(String firstName, String lastName, String email, String phone) {
        return new Tenant(firstName, lastName, email != null ? email : "", phone != null ? phone : "");
    }

    public Tenant fromDto(TenantRequest request) {
        return fromDto(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone());
    }
}
