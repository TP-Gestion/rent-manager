package ar.com.aeb.alquileres.dto.property;

import ar.com.aeb.alquileres.model.Property;
import java.math.BigDecimal;

public class PropertyResponse {

    private Long id;
    private Long buildingId;
    private String floor;
    private Double area;
    private Integer rooms;
    private String unitType;
    private String occupancyStatus;

    public PropertyResponse() {
    }

    public PropertyResponse(Property property) {
        this.id = property.getId();
        this.buildingId = property.getBuilding().getId();
        this.floor = property.getFloor();
        this.area = property.getArea();
        this.rooms = property.getRooms();
        this.unitType = property.getUnitType();
        this.occupancyStatus = property.getOccupancyStatus() == Property.OccupancyStatus.AVAILABLE ? "AVAILABLE" : "OCCUPIED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
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

    public String getOccupancyStatus() {
        return occupancyStatus;
    }

    public void setOccupancyStatus(String occupancyStatus) {
        this.occupancyStatus = occupancyStatus;
    }
}
