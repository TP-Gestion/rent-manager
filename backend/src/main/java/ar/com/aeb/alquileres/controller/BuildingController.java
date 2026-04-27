package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.building.BuildingRequest;
import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.service.BuildingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.com.aeb.alquileres.service.ExpenseService;

@RestController
@RequestMapping("/api/v1/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private ExpenseService expenseService;

    /**
     * CREATE - Add a new building
     */
    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(@Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * READ - Get all buildings
     */
    @GetMapping
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        List<BuildingResponse> buildings = buildingService.getAll();
        return ResponseEntity.ok(buildings);
    }

    /**
     * READ - Get building by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponse> getBuildingById(@PathVariable Long id) {
        BuildingResponse building = buildingService.getById(id);
        return ResponseEntity.ok(building);
    }

    /**
     * UPDATE - Update a building
     */
    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponse> updateBuilding(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE - Remove a building
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * READ - Get total count of buildings
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBuildings() {
        long count = buildingService.count();
        return ResponseEntity.ok(count);
    }

    /**
     * CREATE - Add a new expense to a building
     */
    @PostMapping("/{buildingId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> createBuildingExpense(@PathVariable Long buildingId, @Valid @RequestBody ExpenseRequest request) {
        List<ExpenseResponse> response = expenseService.createBuildingExpense(buildingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
