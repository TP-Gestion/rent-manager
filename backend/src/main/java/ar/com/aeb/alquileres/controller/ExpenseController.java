package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.expense.ExpenseResponse;
import ar.com.aeb.alquileres.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expenses", description = "Operations for managing expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
                                                             @RequestParam(required = false) Long propertyId, @RequestParam(required = false) Long buildingId) {
        List<ExpenseResponse> expenses = expenseService.getExpenses(propertyId, buildingId);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
