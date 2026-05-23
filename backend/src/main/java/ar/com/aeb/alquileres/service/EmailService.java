package ar.com.aeb.alquileres.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBillingEmail(String to, byte[] pdf) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Factura de alquiler");
            helper.setText("Adjunto encontrarás tu factura de alquiler.");

            // Adjuntar el PDF
            helper.addAttachment("factura.pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando mail", e);
        }
    }
}
