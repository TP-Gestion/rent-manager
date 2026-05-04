package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.model.Payment;
import ar.com.aeb.alquileres.model.RentalContract;
import ar.com.aeb.alquileres.model.Tenant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ReceiptService {

    public byte[] generateReceipt(Payment payment) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // Title
            document.add(new Paragraph("RECIBO DE PAGO")
                    .setFont(bold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Receipt number
            document.add(new Paragraph("Recibo N°: " + payment.getId())
                    .setFont(bold)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("Fecha de pago: " + payment.getPaymentDate())
                    .setFont(regular)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20));

            // Tenant info
            Tenant tenant = payment.getProperty().getTenant();
            if (tenant != null) {
                document.add(new Paragraph("Inquilino: " + tenant.getFirstName() + " " + tenant.getLastName())
                        .setFont(regular).setFontSize(12));
                document.add(new Paragraph("Email: " + tenant.getEmail())
                        .setFont(regular).setFontSize(12));
                document.add(new Paragraph("Teléfono: " + tenant.getPhone())
                        .setFont(regular).setFontSize(12).setMarginBottom(15));
            }

            // Property info
            document.add(new Paragraph("Propiedad: Piso " + payment.getProperty().getFloor()
                    + " — " + payment.getProperty().getBuilding().getName())
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Dirección: " + payment.getProperty().getBuilding().getAddress())
                    .setFont(regular).setFontSize(12).setMarginBottom(20));

            // Payment detail table
            Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();

            table.addHeaderCell(new Cell().add(new Paragraph("Concepto").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Importe").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            RentalContract contract = payment.getRentalContract();
            table.addCell(new Cell().add(new Paragraph(
                    "Alquiler — vencimiento " + contract.getDueDate()).setFont(regular)));
            table.addCell(new Cell().add(new Paragraph(
                    "$ " + payment.getTotalAmount()).setFont(regular))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Total row
            table.addCell(new Cell().add(new Paragraph("TOTAL").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("$ " + payment.getTotalAmount()).setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(table);

            // Payment method
            document.add(new Paragraph("Medio de pago: " + payment.getPaymentMethod())
                    .setFont(regular).setFontSize(11).setMarginTop(15));

            if (payment.getNotes() != null && !payment.getNotes().isBlank()) {
                document.add(new Paragraph("Observaciones: " + payment.getNotes())
                        .setFont(regular).setFontSize(11));
            }

            document.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating receipt PDF", e);
        }
    }
}
