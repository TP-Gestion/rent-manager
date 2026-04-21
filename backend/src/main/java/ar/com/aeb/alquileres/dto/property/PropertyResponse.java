package ar.com.aeb.alquileres.dto.property;

import ar.com.aeb.alquileres.model.Property;
import java.math.BigDecimal;

public class PropertyResponse {

    private Long id;
    private String tenantName;
    private String building;
    private String floor;
    private String occupancyStatus;
    private String paymentStatus;
    private String dueDate;
    private BigDecimal totalAmount;

    private String address;
    private Double area;
    private Integer rooms;
    private String unitType;
    private BigDecimal rentalPrice;
    private BigDecimal expenses;
    private String tenantEmail;
    private String tenantPhone;

    public PropertyResponse() {
    }

    public PropertyResponse(Property property) {
        this.id = property.getId();

        if (property.getTenant() != null) {
            this.tenantName = property.getTenant().getFirstName() + " " + property.getTenant().getLastName();
            this.tenantEmail = property.getTenant().getEmail();
            this.tenantPhone = property.getTenant().getPhone();
        } else {
            this.tenantName = "No Tenant";
            this.tenantEmail = null;
            this.tenantPhone = null;
        }

        this.building = property.getBuilding();
        this.floor = property.getFloor();
        this.address = property.getAddress();
        this.area = property.getArea();
        this.rooms = property.getRooms();
        this.unitType = property.getUnitType();
        this.rentalPrice = property.getRentalPrice();
        this.expenses = property.getExpenses();

        this.occupancyStatus = property.getOccupancyStatus() == Property.OccupancyStatus.AVAILABLE ? "AVAILABLE" : "OCCUPIED";

        if (property.getPaymentStatus() == Property.PaymentStatus.PAID) {
            this.paymentStatus = "PAID";
        } else if (property.getPaymentStatus() == Property.PaymentStatus.PENDING) {
            this.paymentStatus = "PENDING";
        } else {
            this.paymentStatus = "OVERDUE";
        }

        this.dueDate = null;

        BigDecimal rental = this.rentalPrice != null ? this.rentalPrice : BigDecimal.ZERO;
        BigDecimal expTotal = this.expenses != null ? this.expenses : BigDecimal.ZERO;
        this.totalAmount = rental.add(expTotal);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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

    public String getOccupancyStatus() {
        return occupancyStatus;
    }

    public void setOccupancyStatus(String occupancyStatus) {
        this.occupancyStatus = occupancyStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public void setTenantEmail(String tenantEmail) {
        this.tenantEmail = tenantEmail;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }
}
