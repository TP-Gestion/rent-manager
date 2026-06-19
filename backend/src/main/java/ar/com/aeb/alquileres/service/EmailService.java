package ar.com.aeb.alquileres.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envía la factura de alquiler en HTML con el PDF adjunto.
     *
     * Se ejecuta en segundo plano (@Async) para no bloquear el request, y captura
     * los errores en vez de propagarlos: si un mail falla, se loguea y el resto
     * del proceso (lote de facturas) sigue funcionando.
     */
    @Async
    public void sendBillingEmail(String to, String tenantName, String period, BigDecimal totalAmount, byte[] pdf) {
        if (to == null || to.isBlank()) {
            log.warn("No se envía factura: el inquilino no tiene email (período {})", period);
            return;
        }

        try {
            Context context = new Context(Locale.of("es"));
            context.setVariable("tenantName", tenantName);
            context.setVariable("period", period);
            context.setVariable("totalAmount", totalAmount);
            String htmlBody = templateEngine.process("billing-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject("Factura de alquiler - " + period);
            helper.setText(htmlBody, true);
            helper.addAttachment("factura-" + period + ".pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
            log.info("Factura enviada a {} (período {})", to, period);
        } catch (MessagingException | MailException e) {
            log.error("Error enviando factura a {} (período {}): {}", to, period, e.getMessage(), e);
        }
    }
}
