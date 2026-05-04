package ar.com.aeb.alquileres.controller;

import ar.com.aeb.alquileres.dto.ApiResponse;
import ar.com.aeb.alquileres.exception.payment.PaymentNotFoundException;
import ar.com.aeb.alquileres.model.Payment;
import ar.com.aeb.alquileres.repository.PaymentRepository;
import ar.com.aeb.alquileres.service.ExcelExportService;
import ar.com.aeb.alquileres.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class InvoiceController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        byte[] pdf = receiptService.generateReceipt(payment);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("recibo-" + paymentId + ".pdf")
                .build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPayments() {
        List<Payment> payments = paymentRepository.findAll();
        byte[] excel = excelExportService.exportPayments(payments);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("pagos.xlsx")
                .build());

        return ResponseEntity.ok().headers(headers).body(excel);
    }
}
