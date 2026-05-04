package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.model.Payment;
import ar.com.aeb.alquileres.model.Tenant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    private static final String[] HEADERS = {
        "ID", "Fecha de Pago", "Inquilino", "Propiedad", "Edificio",
        "Período (vencimiento)", "Monto", "Medio de Pago", "Observaciones"
    };

    public byte[] exportPayments(List<Payment> payments) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Pagos");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Payment payment : payments) {
                Row row = sheet.createRow(rowNum++);
                Tenant tenant = payment.getProperty().getTenant();
                String tenantName = tenant != null
                        ? tenant.getFirstName() + " " + tenant.getLastName()
                        : "-";

                row.createCell(0).setCellValue(payment.getId());
                row.createCell(1).setCellValue(payment.getPaymentDate().toString());
                row.createCell(2).setCellValue(tenantName);
                row.createCell(3).setCellValue("Piso " + payment.getProperty().getFloor());
                row.createCell(4).setCellValue(payment.getProperty().getBuilding().getName());
                row.createCell(5).setCellValue(payment.getRentalContract().getDueDate().toString());
                row.createCell(6).setCellValue(payment.getTotalAmount().doubleValue());
                row.createCell(7).setCellValue(payment.getPaymentMethod().toString());
                row.createCell(8).setCellValue(payment.getNotes() != null ? payment.getNotes() : "");
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel export", e);
        }
    }
}
