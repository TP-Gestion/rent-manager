package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPropertyId(Long propertyId);
}
