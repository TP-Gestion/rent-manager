package ar.com.aeb.alquileres.dto.property;

import ar.com.aeb.alquileres.model.Property;
import java.math.BigDecimal;

public class PropertyResponse {

    private Long id;
    private String nombreInquilino;
    private String edificio;
    private String piso;
    private String estadoOcupacion;
    private String estadoPago;
    private String fechaVencimiento;
    private BigDecimal montoTotal;
    
    private String direccion;
    private Double superficie;
    private Integer ambientes;
    private String tipoUnidad;
    private BigDecimal montoAlquiler;
    private BigDecimal expensas;
    private String correoInquilino;
    private String telefonoInquilino;

    public PropertyResponse() {
    }

    public PropertyResponse(Property property) {
        this.id = property.getId();
        
        if (property.getTenant() != null) {
            this.nombreInquilino = property.getTenant().getFirstName() + " " + property.getTenant().getLastName();
            this.correoInquilino = property.getTenant().getEmail();
            this.telefonoInquilino = property.getTenant().getPhone();
        } else {
            this.nombreInquilino = "Sin Inquilino";
            this.correoInquilino = null;
            this.telefonoInquilino = null;
        }
        
        this.edificio = property.getBuilding();
        this.piso = property.getFloor();
        this.direccion = property.getAddress();
        this.superficie = property.getArea();
        this.ambientes = property.getRooms();
        this.tipoUnidad = property.getUnitType();
        this.montoAlquiler = property.getRentalPrice();
        this.expensas = property.getExpenses();
        
        this.estadoOcupacion = property.getOccupancyStatus() == Property.OccupancyStatus.AVAILABLE ? "LIBRE" : "OCUPADO";
        
        if (property.getPaymentStatus() == Property.PaymentStatus.PAID) {
            this.estadoPago = "PAGADO";
        } else if (property.getPaymentStatus() == Property.PaymentStatus.PENDING) {
            this.estadoPago = "PENDIENTE";
        } else {
            this.estadoPago = "VENCIDO";
        }
        
        this.fechaVencimiento = null;
        
        BigDecimal alquiler = this.montoAlquiler != null ? this.montoAlquiler : BigDecimal.ZERO;
        BigDecimal expTotal = this.expensas != null ? this.expensas : BigDecimal.ZERO;
        this.montoTotal = alquiler.add(expTotal);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreInquilino() { return nombreInquilino; }
    public void setNombreInquilino(String nombreInquilino) { this.nombreInquilino = nombreInquilino; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getPiso() { return piso; }
    public void setPiso(String piso) { this.piso = piso; }

    public String getEstadoOcupacion() { return estadoOcupacion; }
    public void setEstadoOcupacion(String estadoOcupacion) { this.estadoOcupacion = estadoOcupacion; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public Integer getAmbientes() { return ambientes; }
    public void setAmbientes(Integer ambientes) { this.ambientes = ambientes; }

    public String getTipoUnidad() { return tipoUnidad; }
    public void setTipoUnidad(String tipoUnidad) { this.tipoUnidad = tipoUnidad; }

    public BigDecimal getMontoAlquiler() { return montoAlquiler; }
    public void setMontoAlquiler(BigDecimal montoAlquiler) { this.montoAlquiler = montoAlquiler; }

    public BigDecimal getExpensas() { return expensas; }
    public void setExpensas(BigDecimal expensas) { this.expensas = expensas; }

    public String getCorreoInquilino() { return correoInquilino; }
    public void setCorreoInquilino(String correoInquilino) { this.correoInquilino = correoInquilino; }

    public String getTelefonoInquilino() { return telefonoInquilino; }
    public void setTelefonoInquilino(String telefonoInquilino) { this.telefonoInquilino = telefonoInquilino; }
}
