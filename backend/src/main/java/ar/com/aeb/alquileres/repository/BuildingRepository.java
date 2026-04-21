package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Building;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByName(String name);
    Optional<Building> findByAddress(String address);
}
