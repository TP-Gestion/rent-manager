package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.property.PropertyRequest;
import ar.com.aeb.alquileres.dto.property.PropertyResponse;
import ar.com.aeb.alquileres.model.Property;
import ar.com.aeb.alquileres.model.Tenant;
import ar.com.aeb.alquileres.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Create a new property
     */
    public PropertyResponse create(PropertyRequest request) {
        if (propertyRepository.existsByAddressAndBuildingAndFloor(request.getDireccion(), request.getEdificio(), request.getPiso())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La propiedad ya existe con esa dirección, edificio y piso");
        }

        Property property = new Property();
        property.setBuilding(request.getEdificio());
        property.setFloor(request.getPiso());
        property.setArea(request.getSuperficie());
        property.setRooms(request.getAmbientes());
        property.setAddress(request.getDireccion());
        property.setUnitType(request.getTipoUnidad());
        property.setRentalPrice(request.getMontoAlquiler());
        property.setExpenses(request.getExpensas());

        boolean hasTenantName = request.getNombreInquilino() != null && !request.getNombreInquilino().trim().isEmpty();
        if (hasTenantName) {
            if (request.getApellidoInquilino() == null || request.getApellidoInquilino().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Si se ingresa inquilino, el apellido es obligatorio");
            }
            Tenant tenant = new Tenant();
            tenant.setFirstName(request.getNombreInquilino());
            tenant.setLastName(request.getApellidoInquilino());
            tenant.setEmail(request.getCorreoInquilino() != null ? request.getCorreoInquilino() : "");
            tenant.setPhone(request.getTelefonoInquilino() != null ? request.getTelefonoInquilino() : "");
            property.setTenant(tenant);
            property.setOccupancyStatus(Property.OccupancyStatus.OCCUPIED);
        } else {
            property.setOccupancyStatus(Property.OccupancyStatus.AVAILABLE);
        }
        
        property.setPaymentStatus(Property.PaymentStatus.PAID);

        return new PropertyResponse(propertyRepository.save(property));
    }

    /**
     * Get property by ID
     */
    @Transactional(readOnly = true)
    public PropertyResponse getById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada: " + id));
        return new PropertyResponse(property);
    }

    /**
     * Get properties by filters
     */
    @Transactional(readOnly = true)
    public List<PropertyResponse> getByFilters(String building, String status) {
        Property.PaymentStatus paymentStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                if (status.equalsIgnoreCase("PAGADO")) paymentStatus = Property.PaymentStatus.PAID;
                else if (status.equalsIgnoreCase("PENDIENTE")) paymentStatus = Property.PaymentStatus.PENDING;
                else if (status.equalsIgnoreCase("VENCIDO")) paymentStatus = Property.PaymentStatus.OVERDUE;
                else paymentStatus = Property.PaymentStatus.valueOf(status.toUpperCase());
            } catch (Exception e) {
                // Ignore invalid status for now, or could throw BAD_REQUEST
            }
        }
        
        String bFilter = (building != null && !building.trim().isEmpty()) ? building : null;
        
        return propertyRepository.findByFilters(bFilter, paymentStatus)
                .stream().map(PropertyResponse::new).collect(Collectors.toList());
    }

    /**
     * Delete property
     */
    public void delete(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada: " + id);
        }
        propertyRepository.deleteById(id);
    }

    /**
     * Count total properties
     */
    @Transactional(readOnly = true)
    public long count() {
        return propertyRepository.count();
    }
}
