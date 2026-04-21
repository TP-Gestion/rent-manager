package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    boolean existsByAddressAndBuildingAndFloor(String address, String building, String floor);

    @Query("SELECT p FROM Property p WHERE " + "(:building IS NULL OR p.building = :building) AND " + "(:paymentStatus IS NULL OR p.paymentStatus = :paymentStatus)")
    List<Property> findByFilters(@Param("building") String building, @Param("paymentStatus") Property.PaymentStatus paymentStatus);
}
