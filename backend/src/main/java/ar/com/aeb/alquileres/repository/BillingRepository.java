package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByPropertyId(Long propertyId);

    java.util.Optional<Billing> findByPropertyIdAndPeriod(Long propertyId, String period);

    List<Billing> findByRentalContractId(Long rentalContractId);

    Optional<Billing> findByRentalContractIdAndPeriod(Long rentalContractId, String period);
}
