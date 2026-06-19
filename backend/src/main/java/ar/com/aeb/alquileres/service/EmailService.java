package ar.com.aeb.alquileres.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setSubject("Init");
            message.setText("Init");
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dummy@localhost"));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error inicializando conexión SMTP: " + e.getMessage());
        }
    }

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
