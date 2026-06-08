package ar.com.aeb.alquileres.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "TENANTS")
public class Tenant extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean active = true;

    @Column
    private LocalDate deactivatedAt;

    public Tenant() {
    }

    public Tenant(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
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
}
