package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.building.BuildingRequest;
import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.dto.expense.ExpenseRequest;
import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.service.BuildingService;
import ar.com.aeb.alquileres.service.ExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<BuildingResponse>> createBuilding(@Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Building created successfully", response));
    }

    /**
     * READ - Get all buildings
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> getAllBuildings() {
        List<BuildingResponse> buildings = buildingService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Success", buildings));
    }

    /**
     * READ - Get building by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponse>> getBuildingById(@PathVariable Long id) {
        BuildingResponse building = buildingService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", building));
    }

    /**
     * UPDATE - Update a building
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponse>> updateBuilding(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Building updated successfully", response));
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
    public ResponseEntity<ApiResponse<Long>> countBuildings() {
        long count = buildingService.count();
        return ResponseEntity.ok(ApiResponse.success("Success", count));
    }

    /**
     * READ - Get expenses for a building
     */
    @GetMapping("/{buildingId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getBuildingExpenses(@PathVariable Long buildingId) {
        List<ExpenseResponse> response = expenseService.getExpenses(null, buildingId);
        return ResponseEntity.ok(ApiResponse.success("Success", response));
    }

    /**
     * CREATE - Add a new expense to a building
     */
    @PostMapping("/{buildingId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> createBuildingExpense(@PathVariable Long buildingId, @Valid @RequestBody ExpenseRequest request) {
        List<ExpenseResponse> response = expenseService.createBuildingExpense(buildingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Expense created successfully", response));
    }
}
