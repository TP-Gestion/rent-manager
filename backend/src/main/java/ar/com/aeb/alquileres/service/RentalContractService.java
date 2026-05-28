package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;

import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.exception.rentalContract.RentalContractNotFoundException;
import ar.com.aeb.alquileres.exception.rentalContract.DuplicateActiveContractException;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.BillingRepository;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.TenantRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class RentalContractService {

    @Autowired
    private RentalContractRepository rentalContractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BillingRepository billingRepository;

    @org.springframework.beans.factory.annotation.Value("${upload.contracts.path:uploads/contracts}")
    private String uploadPath;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Create a new rental contract
     */
    public RentalContractResponse create(Long propertyId, RentalContractRequest request) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));

        // Validate that no rental contract already exists for this property
        List<RentalContract> existingContracts = rentalContractRepository.findByPropertyId(propertyId);
        if (!existingContracts.isEmpty()) {
            throw new DuplicateActiveContractException("Property with ID " + propertyId + " already has a rental contract.");
        }

        Tenant tenant = property.getTenant();
        if (tenant == null) {
            throw new IllegalStateException("Property has no tenant assigned");
        }

        RentalContract contract = new RentalContract(
                property, request.getAmount(), request.getDueDate()
        );
        contract.setTenant(tenant);

        MultipartFile file = request.getContract();
        if (file != null && !file.isEmpty()) {
            YearMonth now = YearMonth.now();
            String fileName = fileStorageService.storeFile(uploadPath, file, String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()));
            contract.setContractPath(fileName);
        }

        RentalContract saved = rentalContractRepository.save(contract);
        return new RentalContractResponse(saved);
    }

    @Transactional(readOnly = true)
    public Resource getContractResource(Long id) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));

        if (contract.getContractPath() == null) {
            throw new RuntimeException("No contract file found for this record");
        }

        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadPath).resolve(contract.getContractPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + contract.getContractPath());
            }
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Error retrieving file " + contract.getContractPath(), e);
        }
    }

    /**
     * Get contract by ID
     */
    @Transactional(readOnly = true)
    public RentalContractResponse getDetail(Long id) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));
        return new RentalContractResponse(contract);
    }

    /**
     * Get all contracts
     */
    @Transactional(readOnly = true)
    public List<RentalContractResponse> getAll() {
        return rentalContractRepository.findAll().stream().map(RentalContractResponse::new).collect(Collectors.toList());
    }

    /**
     * Get contracts by property
     */
    @Transactional(readOnly = true)
    public List<RentalContractResponse> getByProperty(Long propertyId) {
        propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));

        return rentalContractRepository.findByPropertyId(propertyId).stream().map(RentalContractResponse::new).collect(Collectors.toList());
    }

    /**
     * Update contract
     */
    public RentalContractResponse update(Long id, RentalContractRequest request) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));

        if (request.getAmount() != null) {
            contract.setAmount(request.getAmount());
        }
        if (request.getDueDate() != null) {
            contract.setDueDate(request.getDueDate());
        }

        MultipartFile file = request.getContract();
        if (file != null && !file.isEmpty()) {
            // Delete old file if exists
            if (contract.getContractPath() != null) {
                fileStorageService.deleteFile(uploadPath, contract.getContractPath());
            }

            YearMonth now = YearMonth.now();
            String fileName = fileStorageService.storeFile(uploadPath, file, String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()));
            contract.setContractPath(fileName);
        }

        RentalContract updated = rentalContractRepository.save(contract);
        return new RentalContractResponse(updated);
    }

    /**
     * Delete contract
     */
    public void delete(Long id) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));

        // Delete related billings
        List<Billing> billings = billingRepository.findByRentalContractId(id);
        billingRepository.deleteAll(billings);

        // Delete file if exists
        if (contract.getContractPath() != null) {
            fileStorageService.deleteFile(uploadPath, contract.getContractPath());
        }

        rentalContractRepository.delete(contract);
    }

    /**
     * Change contract status
     */
    public RentalContractResponse updateStatus(Long id, RentalContract.RentalContractStatus status) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));
        contract.setStatus(status);
        RentalContract updated = rentalContractRepository.save(contract);
        return new RentalContractResponse(updated);
    }


}
