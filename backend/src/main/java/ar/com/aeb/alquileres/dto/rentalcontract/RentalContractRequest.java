package ar.com.aeb.alquileres.dto.rentalcontract;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public class RentalContractRequest {

    private Long tenantId;

    @Positive(message = "The amount must be greater than 0")
    private BigDecimal amount;

    private LocalDate dueDate;

    private MultipartFile contract;

    public RentalContractRequest() {
    }

    public RentalContractRequest(Long tenantId, BigDecimal amount, LocalDate dueDate) {
        this.tenantId = tenantId;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public MultipartFile getContract() {
        return contract;
    }

    public void setContract(MultipartFile contract) {
        this.contract = contract;
    }
}
