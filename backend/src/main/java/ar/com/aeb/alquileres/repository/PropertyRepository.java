package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByCity(String city);

    List<Property> findByStatus(Property.PropertyStatus status);

    List<Property> findByCityAndStatus(String city, Property.PropertyStatus status);

    List<Property> findByAddressContainingIgnoreCase(String address);

    List<Property> findByBedroomsGreaterThanEqualAndBathroomsGreaterThanEqual(
            Integer bedrooms, Integer bathrooms);

    List<Property> findByStatusOrderByCreatedAtDesc(Property.PropertyStatus status);
}
