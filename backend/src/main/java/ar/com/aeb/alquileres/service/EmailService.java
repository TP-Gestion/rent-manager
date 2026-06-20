package ar.com.aeb.alquileres.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final Locale AR = Locale.of("es", "AR");

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Flujo normal: se ejecuta en segundo plano y NUNCA propaga (loguea cualquier
     * error) para no cortar el lote de facturas ni romper el request.
     */
    @Async
    public void sendBillingEmail(String to, String tenantName, String period, BigDecimal totalAmount, byte[] pdf) {
        try {
            sendBillingEmailSync(to, tenantName, period, totalAmount, pdf);
            log.info("Factura enviada a {} (período {})", to, period);
        } catch (Exception e) {
            log.error("Error enviando factura a {} (período {}): {}", to, period, e.getMessage(), e);
        }
    }

    /**
     * Envío sincrónico que SÍ propaga el error. Útil para diagnóstico/test, donde
     * queremos ver la causa real en vez de tragárnosla.
     */
    public void sendBillingEmailSync(String to, String tenantName, String period, BigDecimal totalAmount, byte[] pdf)
            throws MessagingException {
        if (to == null || to.isBlank()) {
            throw new MessagingException("El inquilino no tiene email cargado");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject("Factura de alquiler - " + period);
        helper.setText(buildHtmlBody(tenantName, period, totalAmount), true);

        if (pdf != null && pdf.length > 0) {
            helper.addAttachment("factura-" + period + ".pdf", new ByteArrayResource(pdf));
        }

        mailSender.send(message);
    }

    private String buildHtmlBody(String tenantName, String period, BigDecimal totalAmount) {
        String name = tenantName != null ? tenantName : "";
        String amount = totalAmount != null ? NumberFormat.getNumberInstance(AR).format(totalAmount) : "-";
        return """
                <div style="font-family:Arial,Helvetica,sans-serif;color:#1f2933;max-width:600px;">
                  <h2 style="color:#1f3a93;margin:0 0 16px;">Factura de alquiler</h2>
                  <p>Hola <strong>%s</strong>,</p>
                  <p>Te enviamos la factura correspondiente al período <strong>%s</strong>.
                     El detalle completo está en el PDF adjunto.</p>
                  <p style="font-size:16px;">Total a pagar: <strong>$%s</strong></p>
                  <p style="color:#7b8794;font-size:13px;">Mensaje automático, por favor no respondas a este correo.</p>
                </div>
                """.formatted(name, period, amount);
    }
}
