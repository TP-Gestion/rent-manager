package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.exception.expense.ExpenseNotFoundException;
import ar.com.aeb.alquileres.exception.building.BuildingNotFoundException;
import ar.com.aeb.alquileres.exception.expense.InvalidExpenseRequestException;
import ar.com.aeb.alquileres.exception.property.PropertyNotFoundException;
import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.model.Expense;
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

    public List<ExpenseResponse> createBuildingExpense(Long buildingId, ExpenseRequest request) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(() -> new BuildingNotFoundException(buildingId));

        List<Property> properties = building.getProperties();
        if (properties.isEmpty()) {
            throw new InvalidExpenseRequestException("Cannot create an expense for a building with no properties.");
        }

        Expense expenseTemplate = createExpenseTemplate(request, building);
        expenseRepository.save(expenseTemplate);

        BigDecimal amountPerProperty = request.getAmount().divide(new BigDecimal(properties.size()), 2, RoundingMode.HALF_UP);
        List<PropertyExpense> instancesToSave = new ArrayList<>();
        for (Property property : properties) {
            PropertyExpense instance = new PropertyExpense(expenseTemplate, property, amountPerProperty);
            instancesToSave.add(instance);
        }

        List<PropertyExpense> savedInstances = propertyExpenseRepository.saveAll(instancesToSave);
        return savedInstances.stream().map(ExpenseResponse::new).collect(Collectors.toList());
    }

    private Expense createExpenseTemplate(ExpenseRequest request, Building building) {
        Expense expenseTemplate = new Expense();
        expenseTemplate.setBuilding(building);
        expenseTemplate.setType(request.getType());
        expenseTemplate.setDescription(request.getDescription());
        expenseTemplate.setDueDate(request.getDueDate());
        expenseTemplate.setAmount(request.getAmount());
        return expenseTemplate;
    }
}
