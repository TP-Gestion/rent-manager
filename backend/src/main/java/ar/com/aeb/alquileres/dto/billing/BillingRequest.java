package ar.com.aeb.alquileres.dto.billing;

import java.util.List;

public class BillingRequest {

    private List<Long> propertyIds;

    public List<Long> getPropertyIds() {
        return propertyIds;
    }

    public void setPropertyIds(List<Long> propertyIds) {
        this.propertyIds = propertyIds;
    }
}
