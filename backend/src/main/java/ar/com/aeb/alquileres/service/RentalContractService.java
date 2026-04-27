package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractRequest;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;

import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.exception.rentalContract.RentalContractNotFoundException;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.repository.RentalContractRepository;
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
    private PropertyRepository propertyRepository;

    /**
     * Create a new rental contract
     */
    public RentalContractResponse create(Long propertyId, RentalContractRequest request) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));

        RentalContract contract = new RentalContract(
                property, request.getAmount(), request.getDueDate()
        );

        RentalContract saved = rentalContractRepository.save(contract);
        return new RentalContractResponse(saved);
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

        contract.setAmount(request.getAmount());
        contract.setDueDate(request.getDueDate());

        RentalContract updated = rentalContractRepository.save(contract);
        return new RentalContractResponse(updated);
    }

    /**
     * Delete contract
     */
    public void delete(Long id) {
        RentalContract contract = rentalContractRepository.findById(id).orElseThrow(() -> new RentalContractNotFoundException(id));
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
