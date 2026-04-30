package ar.com.aeb.alquileres.dto.property;

import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.dto.tenant.TenantResponse;
import ar.com.aeb.alquileres.dto.rentalcontract.RentalContractResponse;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.RentalContract;

public class PropertyDetailsResponse {

    private Long id;
    private BuildingResponse building;
    private String floor;
    private Double area;
    private Integer rooms;
    private String unitType;
    private String occupancyStatus;
    private TenantResponse tenant;
    private RentalContractResponse activeContract;

    public PropertyDetailsResponse() {
    }

    public PropertyDetailsResponse(Property property, RentalContract activeContract) {
        this.id = property.getId();
        this.building = property.getBuilding() != null ? new BuildingResponse(property.getBuilding()) : null;
        this.floor = property.getFloor();
        this.area = property.getArea();
        this.rooms = property.getRooms();
        this.unitType = property.getUnitType();
        this.occupancyStatus = property.getOccupancyStatus() != null ? property.getOccupancyStatus().name() : null;
        this.tenant = property.getTenant() != null ? new TenantResponse(property.getTenant()) : null;
        this.activeContract = activeContract != null ? new RentalContractResponse(activeContract) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BuildingResponse getBuilding() {
        return building;
    }

    public void setBuilding(BuildingResponse building) {
        this.building = building;
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

    public TenantResponse getTenant() {
        return tenant;
    }

    public void setTenant(TenantResponse tenant) {
        this.tenant = tenant;
    }

    public RentalContractResponse getActiveContract() {
        return activeContract;
    }

    public void setActiveContract(RentalContractResponse activeContract) {
        this.activeContract = activeContract;
    }
}
