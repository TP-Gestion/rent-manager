package ar.com.aeb.alquileres.dto.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertySummaryResponse {

    private Long id;
    private String edificio;
    private String piso;
    private String tipoUnidad;
    private String estadoOcupacion;
    private String estadoPago;
    private TenantSummary inquilino;
    private LocalDate fechaVencimiento;
    private BigDecimal montoTotal;

    public PropertySummaryResponse() {
    }

    public static class TenantSummary {
        private Long id;
        private String nombre;
        private String apellido;

        public TenantSummary(Long id, String nombre, String apellido) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getTipoUnidad() {
        return tipoUnidad;
    }

    public void setTipoUnidad(String tipoUnidad) {
        this.tipoUnidad = tipoUnidad;
    }

    public String getEstadoOcupacion() {
        return estadoOcupacion;
    }

    public void setEstadoOcupacion(String estadoOcupacion) {
        this.estadoOcupacion = estadoOcupacion;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public TenantSummary getInquilino() {
        return inquilino;
    }

    public void setInquilino(TenantSummary inquilino) {
        this.inquilino = inquilino;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
