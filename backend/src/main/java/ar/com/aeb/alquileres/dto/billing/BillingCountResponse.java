package ar.com.aeb.alquileres.dto.billing;

public class BillingCountResponse {

    private int count;

    public BillingCountResponse(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
