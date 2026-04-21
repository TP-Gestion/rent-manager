package ar.com.aeb.alquileres.dto.property;

import ar.com.aeb.alquileres.model.Property;
import java.math.BigDecimal;

public class PropertyResponse {

    private Long id;
    private String building;
    private String floor;
    private String occupancyStatus;
    private String address;
    private Double area;
    private Integer rooms;
    private String unitType;
    private BigDecimal expenses;

    public PropertyResponse() {
    }

    public PropertyResponse(Property property) {
        this.id = property.getId();
        this.building = property.getBuilding();
        this.floor = property.getFloor();
        this.address = property.getAddress();
        this.area = property.getArea();
        this.rooms = property.getRooms();
        this.unitType = property.getUnitType();
        this.expenses = property.getExpenses();
        this.occupancyStatus = property.getOccupancyStatus() == Property.OccupancyStatus.AVAILABLE ? "AVAILABLE" : "OCCUPIED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }
}
