package ar.com.aeb.alquileres.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PROPERTIES")
public class Property extends BaseEntity {

    @Column
    private String building;

    @Column
    private String floor;

    @Column
    private Double area;

    @Column
    private Integer rooms;

    @Column
    private String address;

    @Column
    private String unitType;

    private BigDecimal rentalPrice;

    private BigDecimal expenses;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column
    private OccupancyStatus occupancyStatus = OccupancyStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    public Property() {
    }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public BigDecimal getRentalPrice() { return rentalPrice; }
    public void setRentalPrice(BigDecimal rentalPrice) { this.rentalPrice = rentalPrice; }

    public BigDecimal getExpenses() { return expenses; }
    public void setExpenses(BigDecimal expenses) { this.expenses = expenses; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public OccupancyStatus getOccupancyStatus() { return occupancyStatus; }
    public void setOccupancyStatus(OccupancyStatus occupancyStatus) { this.occupancyStatus = occupancyStatus; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public enum OccupancyStatus {
        AVAILABLE, OCCUPIED
    }

    public enum PaymentStatus {
        PAID, PENDING, OVERDUE
    }
}
