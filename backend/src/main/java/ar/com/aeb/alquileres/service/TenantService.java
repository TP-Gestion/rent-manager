package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantSummaryResponse;
import ar.com.aeb.alquileres.exception.tenant.DuplicateEmailException;
import ar.com.aeb.alquileres.exception.tenant.DuplicatePhoneException;
import ar.com.aeb.alquileres.exception.tenant.TenantAlreadyInactiveException;
import ar.com.aeb.alquileres.exception.tenant.TenantNotFoundException;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import ar.com.aeb.alquileres.repository.TenantRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    private RentalContractRepository rentalContractRepository;

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
     * Resumen del inquilino: sus datos + deuda por propiedad + deuda total.
     */
    @Transactional(readOnly = true)
    public TenantSummaryResponse getSummary(Long id) {
        Tenant tenant = findById(id);

        TenantSummaryResponse summary = new TenantSummaryResponse();
        summary.setFirstName(tenant.getFirstName());
        summary.setLastName(tenant.getLastName());
        summary.setEmail(tenant.getEmail());
        summary.setPhone(tenant.getPhone());

        BigDecimal pendingAmount = BigDecimal.ZERO;
        List<TenantSummaryResponse.PropertySummary> properties = new ArrayList<>();

        for (Property property : propertyRepository.findByTenantId(id)) {
            TenantSummaryResponse.PropertySummary ps = buildPropertySummary(property);
            properties.add(ps);
            pendingAmount = pendingAmount.add(ps.getExpenses()).add(ps.getRentalAmount());
        }

        summary.setProperties(properties);
        summary.setPendingAmount(pendingAmount);
        return summary;
    }

    /**
     * Calcula la deuda (expensas y alquiler PENDIENTES), estado y vencimiento de una propiedad.
     * Misma lógica que PropertyService.buildSummary, pero separando expensas de alquiler.
     */
    private TenantSummaryResponse.PropertySummary buildPropertySummary(Property property) {
        BigDecimal expensesPending = BigDecimal.ZERO;
        boolean hasPending = false;
        boolean hasOverdue = false;

        for (PropertyExpense pe : property.getPropertyExpenses()) {
            if (pe.getStatus() != PropertyExpense.PropertyExpenseStatus.PAID) {
                expensesPending = expensesPending.add(pe.getAmount());
                hasPending = true;
                if (pe.getStatus() == PropertyExpense.PropertyExpenseStatus.OVERDUE) {
                    hasOverdue = true;
                }
            }
        }

        BigDecimal rentPending = BigDecimal.ZERO;
        LocalDate earliestDueDate = null;
        for (RentalContract contract : rentalContractRepository.findByPropertyId(property.getId())) {
            if (contract.getStatus() != RentalContract.RentalContractStatus.PAID) {
                rentPending = rentPending.add(contract.getAmount());
                hasPending = true;
                if (contract.getStatus() == RentalContract.RentalContractStatus.OVERDUE) {
                    hasOverdue = true;
                }
                if (earliestDueDate == null || contract.getDueDate().isBefore(earliestDueDate)) {
                    earliestDueDate = contract.getDueDate();
                }
            }
        }

        TenantSummaryResponse.PropertySummary ps = new TenantSummaryResponse.PropertySummary();
        ps.setId(property.getId());
        ps.setExpenses(expensesPending);
        ps.setRentalAmount(rentPending);
        ps.setStatus(hasOverdue ? "OVERDUE" : (hasPending ? "PENDING" : "PAID"));
        ps.setDueDate(earliestDueDate);
        ps.setBuilding(property.getBuilding() != null ? property.getBuilding().getName() : null);
        ps.setFloor(property.getFloor());
        ps.setUnitType(property.getUnitType());
        return ps;
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
