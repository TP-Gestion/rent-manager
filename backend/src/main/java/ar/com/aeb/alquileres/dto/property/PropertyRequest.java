package ar.com.aeb.alquileres.dto.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PropertyRequest {

    @NotBlank(message = "El edificio es obligatorio")
    private String edificio;

    @NotBlank(message = "El piso es obligatorio")
    private String piso;

    @NotNull(message = "La superficie es obligatoria")
    private Double superficie;

    @NotNull(message = "La cantidad de ambientes es obligatoria")
    private Integer ambientes;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El tipo de unidad es obligatorio")
    private String tipoUnidad;

    private BigDecimal montoAlquiler;

    private BigDecimal expensas;

    private String nombreInquilino;
    private String apellidoInquilino;
    private String correoInquilino;
    private String telefonoInquilino;

    public PropertyRequest() {
    }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getPiso() { return piso; }
    public void setPiso(String piso) { this.piso = piso; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public Integer getAmbientes() { return ambientes; }
    public void setAmbientes(Integer ambientes) { this.ambientes = ambientes; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipoUnidad() { return tipoUnidad; }
    public void setTipoUnidad(String tipoUnidad) { this.tipoUnidad = tipoUnidad; }

    public BigDecimal getMontoAlquiler() { return montoAlquiler; }
    public void setMontoAlquiler(BigDecimal montoAlquiler) { this.montoAlquiler = montoAlquiler; }

    public BigDecimal getExpensas() { return expensas; }
    public void setExpensas(BigDecimal expensas) { this.expensas = expensas; }

    public String getNombreInquilino() { return nombreInquilino; }
    public void setNombreInquilino(String nombreInquilino) { this.nombreInquilino = nombreInquilino; }

    public String getApellidoInquilino() { return apellidoInquilino; }
    public void setApellidoInquilino(String apellidoInquilino) { this.apellidoInquilino = apellidoInquilino; }

    public String getCorreoInquilino() { return correoInquilino; }
    public void setCorreoInquilino(String correoInquilino) { this.correoInquilino = correoInquilino; }

    public String getTelefonoInquilino() { return telefonoInquilino; }
    public void setTelefonoInquilino(String telefonoInquilino) { this.telefonoInquilino = telefonoInquilino; }
}

