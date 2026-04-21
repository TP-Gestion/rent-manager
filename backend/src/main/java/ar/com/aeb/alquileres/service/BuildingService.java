package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.building.BuildingRequest;
import ar.com.aeb.alquileres.dto.building.BuildingResponse;
import ar.com.aeb.alquileres.model.Building;
import ar.com.aeb.alquileres.repository.BuildingRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    public BuildingResponse create(BuildingRequest request) {
        Building building = new Building(request.getName(), request.getAddress());
        Building saved = buildingRepository.save(building);
        return new BuildingResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BuildingResponse> getAll() {
        return buildingRepository.findAll().stream().map(BuildingResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuildingResponse getById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found with id: " + id));
        return new BuildingResponse(building);
    }

    public BuildingResponse update(Long id, BuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found with id: " + id));
        
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        
        Building updated = buildingRepository.save(building);
        return new BuildingResponse(updated);
    }

    public void delete(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found with id: " + id));
        buildingRepository.delete(building);
    }

    @Transactional(readOnly = true)
    public long count() {
        return buildingRepository.count();
    }
}
