package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.dto.billing.BillablePropertyResponse;
import ar.com.aeb.alquileres.dto.billing.BillingCountResponse;
import ar.com.aeb.alquileres.dto.billing.BillingRequest;
import ar.com.aeb.alquileres.model.Billing;
import ar.com.aeb.alquileres.service.BillingService;
import ar.com.aeb.alquileres.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/properties/billable")
    public ResponseEntity<ApiResponse<List<BillablePropertyResponse>>> getBillableProperties() {
        List<BillablePropertyResponse> properties = billingService.getBillableProperties();
        return ResponseEntity.ok(ApiResponse.success("Success", properties));
    }

    @PostMapping("/billings")
    public ResponseEntity<ApiResponse<BillingCountResponse>> createBillings(@RequestBody BillingRequest request) {
        BillingCountResponse response = billingService.createBillings(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "Billings created successfully", response));
    }

    @GetMapping("/billings/export")
    public ResponseEntity<byte[]> exportBillings() {
        List<Billing> billings = billingService.getAllBillings();
        byte[] excel = excelExportService.exportBillings(billings);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("facturas.xlsx").build());

        return ResponseEntity.ok().headers(headers).body(excel);
    }
}
