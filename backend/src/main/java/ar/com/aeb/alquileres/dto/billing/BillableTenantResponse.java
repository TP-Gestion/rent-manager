package ar.com.aeb.alquileres.dto.billing;

import ar.com.aeb.alquileres.model.Tenant;

public class BillableTenantResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public BillableTenantResponse(Tenant tenant) {
        if (tenant != null) {
            this.firstName = tenant.getFirstName();
            this.lastName = tenant.getLastName();
            this.email = tenant.getEmail();
            this.phone = tenant.getPhone();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
