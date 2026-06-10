package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.expense.BuildingExpenseResponse;
import ar.com.aeb.alquileres.dto.expense.CreateBuildingExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.exception.expense.ExpenseNotFoundException;
import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Expense;
import ar.com.aeb.alquileres.model.Expense.ExpenseFrequency;
import ar.com.aeb.alquileres.model.Expense.ExpenseType;
import ar.com.aeb.alquileres.model.PropertyExpense;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import ar.com.aeb.alquileres.repository.ExpenseRepository;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import ar.com.aeb.alquileres.repository.PropertyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyExpenseRepository propertyExpenseRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(Long propertyId, Long buildingId) {
        List<PropertyExpense> instances;
        if (propertyId != null) {
            if (!propertyRepository.existsById(propertyId)) throw new PropertyNotFoundException(propertyId);
            instances = propertyExpenseRepository.findByPropertyId(propertyId);
        } else if (buildingId != null) {
            if (!buildingRepository.existsById(buildingId)) throw new BuildingNotFoundException(buildingId);
            instances = propertyExpenseRepository.findByProperty_Building_Id(buildingId);
        } else {
            instances = propertyExpenseRepository.findAll();
        }
        return instances.stream().map(ExpenseResponse::new).collect(Collectors.toList());
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        expenseRepository.deleteById(id);
    }

    public List<ExpenseResponse> createPropertyExpense(Long propertyId, ExpenseRequest request) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));

        Expense expenseTemplate = createExpenseTemplate(request, property.getBuilding());
        expenseRepository.save(expenseTemplate);

        PropertyExpense instance = new PropertyExpense(expenseTemplate, property, request.getAmount());
        PropertyExpense savedInstance = propertyExpenseRepository.save(instance);

        return List.of(new ExpenseResponse(savedInstance));
    }

    // ---- Gastos a nivel edificio (consumidos por el front) ----

    @Transactional(readOnly = true)
    public List<BuildingExpenseResponse> getBuildingExpenseItems(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new BuildingNotFoundException(buildingId);
        }
        return expenseRepository.findByBuildingId(buildingId).stream().map(BuildingExpenseResponse::new).collect(Collectors.toList());
    }

    public BuildingExpenseResponse createBuildingExpense(Long buildingId, CreateBuildingExpenseRequest request) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(() -> new BuildingNotFoundException(buildingId));

        Expense expense = new Expense();
        expense.setBuilding(building);
        expense.setType(request.getType());
        expense.setFrequency(deriveFrequency(request.getType()));
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getConcept());
        expense.setAmount(request.getAmount());
        expenseRepository.save(expense);

        // Mantiene el reparto por propiedad para que billing/pagos sigan funcionando.
        splitAmongProperties(expense, building.getProperties(), request.getAmount());

        return new BuildingExpenseResponse(expense);
    }

    public BuildingExpenseResponse updateBuildingExpense(Long buildingId, Long expenseId, BigDecimal newAmount) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        if (expense.getBuilding() == null || !expense.getBuilding().getId().equals(buildingId)) {
            throw new ExpenseNotFoundException(expenseId);
        }

        expense.setAmount(newAmount);
        expenseRepository.save(expense);

        // Re-reparte el nuevo monto entre las instancias por propiedad existentes.
        List<PropertyExpense> instances = propertyExpenseRepository.findByExpenseId(expenseId);
        if (!instances.isEmpty()) {
            BigDecimal amountPerProperty = newAmount.divide(new BigDecimal(instances.size()), 2, RoundingMode.HALF_UP);
            for (PropertyExpense instance : instances) {
                instance.setAmount(amountPerProperty);
            }
            propertyExpenseRepository.saveAll(instances);
        }

        return new BuildingExpenseResponse(expense);
    }

    private void splitAmongProperties(Expense expense, List<Property> properties, BigDecimal totalAmount) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        BigDecimal amountPerProperty = totalAmount.divide(new BigDecimal(properties.size()), 2, RoundingMode.HALF_UP);
        List<PropertyExpense> instancesToSave = new ArrayList<>();
        for (Property property : properties) {
            instancesToSave.add(new PropertyExpense(expense, property, amountPerProperty));
        }
        propertyExpenseRepository.saveAll(instancesToSave);
    }

    private ExpenseFrequency deriveFrequency(ExpenseType type) {
        return type == ExpenseType.ORDINARIA ? ExpenseFrequency.MENSUAL : ExpenseFrequency.UNICA;
    }

    private Expense createExpenseTemplate(ExpenseRequest request, Building building) {
        Expense expenseTemplate = new Expense();
        expenseTemplate.setBuilding(building);
        expenseTemplate.setType(request.getType());
        expenseTemplate.setFrequency(deriveFrequency(request.getType()));
        expenseTemplate.setDescription(request.getDescription());
        expenseTemplate.setDueDate(request.getDueDate());
        expenseTemplate.setAmount(request.getAmount());
        return expenseTemplate;
    }
}
