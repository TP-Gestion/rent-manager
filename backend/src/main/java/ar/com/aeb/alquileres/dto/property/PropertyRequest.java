package ar.com.aeb.alquileres.dto.property;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PropertyRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    private String postalCode;

    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 1, message = "Must have at least 1 bedroom")
    private Integer bedrooms;

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 1, message = "Must have at least 1 bathroom")
    private Integer bathrooms;

    @NotNull(message = "Rental price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rental price must be greater than 0")
    private BigDecimal rentalPrice;

    private String description;

    public PropertyRequest() {
    }

    public PropertyRequest(String address, String city, String province, Integer bedrooms, Integer bathrooms, BigDecimal rentalPrice) {
        this.address = address;
        this.city = city;
        this.province = province;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.rentalPrice = rentalPrice;
    }

    // Getters y Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
