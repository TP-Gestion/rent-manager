package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    List<RentalContract> findByPropertyId(Long propertyId);

    List<RentalContract> findByStatus(RentalContract.RentalContractStatus status);

    long countByStatusAndPropertyId(RentalContract.RentalContractStatus status, Long propertyId);

    List<RentalContract> findByPropertyIdAndStatus(Long propertyId, RentalContract.RentalContractStatus status);
}
