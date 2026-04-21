package ar.com.aeb.alquileres.dto.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PropertyRequest {

    @NotNull(message = "Building ID is required")
    private Long buildingId;

    @NotBlank(message = "Floor is required")
    private String floor;

    @NotNull(message = "Area is required")
    private Double area;

    @NotNull(message = "Number of rooms is required")
    private Integer rooms;

    @NotBlank(message = "Unit type is required")
    private String unitType;

    public PropertyRequest() {
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
}
