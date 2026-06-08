package ar.com.aeb.alquileres.dto.billing;

import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.model.Tenant;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BillingResponse {

    private Long id;
    private String period;
    private String status;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private TenantInfo tenant;

    public BillingResponse() {
    }

    public BillingResponse(Billing billing) {
        this.id = billing.getId();
        this.period = billing.getPeriod();
        this.status = billing.getStatus().name();
        this.amount = billing.getTotalAmount();
        this.dueDate = billing.getDueDate();
        this.paymentDate = billing.getPayment() != null
                ? billing.getPayment().getPaymentDate()
                : null;
        // Historical tenant snapshot: the tenant that was billed, independent of the
        // current Property-Tenant relation. Null for billings created before this feature.
        this.tenant = billing.getTenant() != null ? new TenantInfo(billing.getTenant()) : null;
    }

    /**
     * Minimal historical view of the tenant that was billed.
     */
    public static class TenantInfo {
        private String firstName;
        private String lastName;

        public TenantInfo() {
        }

        public TenantInfo(Tenant tenant) {
            this.firstName = tenant.getFirstName();
            this.lastName = tenant.getLastName();
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public TenantInfo getTenant() {
        return tenant;
    }

    public void setTenant(TenantInfo tenant) {
        this.tenant = tenant;
    }
}
