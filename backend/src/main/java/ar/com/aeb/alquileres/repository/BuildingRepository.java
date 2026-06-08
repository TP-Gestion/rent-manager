package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.Building;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByName(String name);

    Optional<Building> findByAddress(String address);

    @Query("SELECT DISTINCT p.building FROM Property p WHERE p.tenant.id = :tenantId")
    List<Building> findDistinctByTenantId(@Param("tenantId") Long tenantId);
}
