package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Building;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findByBuildingAndFloor(Building building, String floor);
}
