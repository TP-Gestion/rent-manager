package ar.com.aeb.alquileres.dto.tenant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Resumen para la pantalla del inquilino: sus datos + el detalle de deuda por
 * cada propiedad que alquila, y la deuda total (pendingAmount).
 */
public class TenantSummaryResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private BigDecimal pendingAmount = BigDecimal.ZERO;
    private List<PropertySummary> properties = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public List<PropertySummary> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertySummary> properties) {
        this.properties = properties;
    }

    /**
     * Deuda y datos de una propiedad puntual del inquilino.
     * expenses y rentalAmount son los montos PENDIENTES (no pagados).
     */
    public static class PropertySummary {
        private Long id;
        private BigDecimal expenses;
        private BigDecimal rentalAmount;
        private String status;
        private LocalDate dueDate;
        private String building;
        private String floor;
        private String unitType;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getExpenses() {
            return expenses;
        }

        public void setExpenses(BigDecimal expenses) {
            this.expenses = expenses;
        }

        public BigDecimal getRentalAmount() {
            return rentalAmount;
        }

        public void setRentalAmount(BigDecimal rentalAmount) {
            this.rentalAmount = rentalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getUnitType() {
            return unitType;
        }

        public void setUnitType(String unitType) {
            this.unitType = unitType;
        }
    }
}
