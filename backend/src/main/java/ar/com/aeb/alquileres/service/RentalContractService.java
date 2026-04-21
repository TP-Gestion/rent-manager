package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.exception.PropertyNotFoundException;
import ar.com.aeb.alquileres.exception.TenantNotFoundException;
import ar.com.aeb.alquileres.exception.ResourceNotFoundException;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import ar.com.aeb.alquileres.repository.TenantRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentalContractService {

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Create a new rental contract
     */
    public RentalContractResponse create(RentalContractRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new TenantNotFoundException(request.getTenantId()));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(request.getPropertyId()));

        RentalContract contract = new RentalContract(
                tenant,
                property,
                request.getStartDate(),
                request.getEndDate(),
                request.getMonthlyRent()
        );

        RentalContract saved = rentalContractRepository.save(contract);
        return new RentalContractResponse(saved);
    }

    /**
     * Get contract by ID
     */
    @Transactional(readOnly = true)
    public RentalContractResponse getDetail(Long id) {
        RentalContract contract = rentalContractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found with id: " + id));
        return new RentalContractResponse(contract);
    }

    /**
     * Get all contracts
     */
    @Transactional(readOnly = true)
    public List<RentalContractResponse> getAll() {
        return rentalContractRepository.findAll().stream()
                .map(RentalContractResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get contracts by tenant
     */
    @Transactional(readOnly = true)
    public List<RentalContractResponse> getByTenant(Long tenantId) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));

        return rentalContractRepository.findByTenantId(tenantId).stream()
                .map(RentalContractResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get contracts by property
     */
    @Transactional(readOnly = true)
    public List<RentalContractResponse> getByProperty(Long propertyId) {
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));

        return rentalContractRepository.findByPropertyId(propertyId).stream()
                .map(RentalContractResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Update contract
     */
    public RentalContractResponse update(Long id, RentalContractRequest request) {
        RentalContract contract = rentalContractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found with id: " + id));

        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setMonthlyRent(request.getMonthlyRent());

        RentalContract updated = rentalContractRepository.save(contract);
        return new RentalContractResponse(updated);
    }

    /**
     * Delete contract
     */
    public void delete(Long id) {
        RentalContract contract = rentalContractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found with id: " + id));
        rentalContractRepository.delete(contract);
    }

    /**
     * Change contract status
     */
    public RentalContractResponse updateStatus(Long id, RentalContract.ContractStatus status) {
        RentalContract contract = rentalContractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found with id: " + id));
        contract.setStatus(status);
        RentalContract updated = rentalContractRepository.save(contract);
        return new RentalContractResponse(updated);
    }
}
