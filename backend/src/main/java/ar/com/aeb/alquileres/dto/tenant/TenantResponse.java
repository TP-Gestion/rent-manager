package ar.com.aeb.alquileres.dto.tenant;

import ar.com.aeb.alquileres.model.Tenant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TenantResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean active;
    private LocalDate deactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TenantResponse() {
    }

    public TenantResponse(Tenant tenant) {
        this.id = tenant.getId();
        this.firstName = tenant.getFirstName();
        this.lastName = tenant.getLastName();
        this.email = tenant.getEmail();
        this.phone = tenant.getPhone();
        this.active = tenant.isActive();
        this.deactivatedAt = tenant.getDeactivatedAt();
        this.createdAt = tenant.getCreatedAt();
        this.updatedAt = tenant.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDate deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
