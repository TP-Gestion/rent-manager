package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    List<RentalContract> findByTenantId(Long tenantId);

    List<RentalContract> findByPropertyId(Long propertyId);

    List<RentalContract> findByStatus(RentalContract.ContractStatus status);

    Optional<RentalContract> findByTenantIdAndPropertyId(Long tenantId, Long propertyId);

    List<RentalContract> findByStatusAndTenantId(RentalContract.ContractStatus status, Long tenantId);

    long countByStatusAndTenantId(RentalContract.ContractStatus status, Long tenantId);

    long countByStatusAndPropertyId(RentalContract.ContractStatus status, Long propertyId);

    List<RentalContract> findByPropertyIdAndStatus(Long propertyId, RentalContract.ContractStatus status);
}
