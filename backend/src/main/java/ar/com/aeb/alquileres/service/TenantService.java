package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.tenant.TenantRequest;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.exception.DuplicateEmailException;
import ar.com.aeb.alquileres.exception.DuplicatePhoneException;
import ar.com.aeb.alquileres.exception.TenantNotFoundException;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.TenantRepository;
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

    /**
     * Create a new tenant
     */
    public TenantResponse create(TenantRequest request) {
        // Validate if email already exists
        if (tenantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Validate if phone already exists
        if (tenantRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new DuplicatePhoneException(request.getPhone());
        }

        Tenant tenant = new Tenant(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhone()
        );

        Tenant saved = tenantRepository.save(tenant);
        return new TenantResponse(saved);
    }

    /**
     * Get tenant by ID - Detail endpoint
     */
    @Transactional(readOnly = true)
    public TenantResponse getDetail(Long id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new TenantNotFoundException(id));
        return new TenantResponse(tenant);
    }

    /**
     * Get all tenants
     */
    @Transactional(readOnly = true)
    public List<TenantResponse> getAll() {
        return tenantRepository.findAll().stream()
            .map(TenantResponse::new)
            .collect(Collectors.toList());
    }

    /**
     * Update tenant
     */
    public TenantResponse update(Long id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new TenantNotFoundException(id));

        tenant.setFirstName(request.getFirstName());
        tenant.setLastName(request.getLastName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());

        Tenant updated = tenantRepository.save(tenant);
        return new TenantResponse(updated);
    }

    /**
     * Delete tenant
     */
    public void delete(Long id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new TenantNotFoundException(id));
        tenantRepository.delete(tenant);
    }
}
