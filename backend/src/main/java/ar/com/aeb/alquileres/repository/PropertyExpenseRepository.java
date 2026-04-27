package ar.com.aeb.alquileres.repository;

import ar.com.aeb.alquileres.model.PropertyExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyExpenseRepository extends JpaRepository<PropertyExpense, Long> {
    List<PropertyExpense> findByPropertyId(Long propertyId);

    List<PropertyExpense> findByExpenseId(Long expenseId);

    List<PropertyExpense> findByProperty_Building_Id(Long buildingId);
}
