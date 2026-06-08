package ar.com.aeb.alquileres.model;

import ar.com.aeb.alquileres.model.Expense.ExpenseType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte el enum ExpenseType desde/hacia la columna de la DB.
 *
 * A diferencia de @Enumerated(STRING), tolera valores antiguos (el enum anterior:
 * MAINTENANCE/REPAIR/UTILITIES/TAXES/ADMINISTRATION) traduciéndolos al dominio
 * nuevo en vez de tirar excepción al leerlos. Así no hace falta migrar la DB.
 */
@Converter
public class ExpenseTypeConverter implements AttributeConverter<ExpenseType, String> {

    @Override
    public String convertToDatabaseColumn(ExpenseType type) {
        return type == null ? null : type.name();
    }

    @Override
    public ExpenseType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case "ORDINARIA":
                return ExpenseType.ORDINARIA;
            case "EXTRAORDINARIA":
                return ExpenseType.EXTRAORDINARIA;
            // Valores legacy del enum anterior (gastos puntuales):
            case "MAINTENANCE":
            case "REPAIR":
                return ExpenseType.EXTRAORDINARIA;
            // Valores legacy del enum anterior (recurrentes):
            case "UTILITIES":
            case "TAXES":
            case "ADMINISTRATION":
                return ExpenseType.ORDINARIA;
            // Cualquier otro valor desconocido: default seguro, no explota.
            default:
                return ExpenseType.ORDINARIA;
        }
    }
}
