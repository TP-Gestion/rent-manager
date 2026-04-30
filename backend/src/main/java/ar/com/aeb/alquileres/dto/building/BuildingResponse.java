package ar.com.aeb.alquileres.dto.building;

import ar.com.aeb.alquileres.model.Building;

public class BuildingResponse {

    private Long id;
    private String name;
    private String address;

    public BuildingResponse() {
    }

    public BuildingResponse(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.address = building.getAddress();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
