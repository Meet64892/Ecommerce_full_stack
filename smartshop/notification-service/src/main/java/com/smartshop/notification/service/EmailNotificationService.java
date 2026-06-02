package com.smartshop.notification.service;

import com.smartshop.notification.template.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * EmailNotificationService - Sends or mocks HTML email notifications.
 *
 * <h2>Purpose</h2>
 * Email delivery is an external side effect, so this implementation can run in mock mode for local development. In
 * production the same service can send through JavaMail without changing Kafka consumer code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Mock mode: Logs rendered email instead of requiring an SMTP server.</li>
 *   <li>Log levels: info records successful behavior; error records delivery failures.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Kafka consumers invoke this service after parsing order or user events.
 *
 * @see NotificationService
 * @author SmartShop Team
 */
@Slf4j
@Service
public class EmailNotificationService implements NotificationService {
    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    private final boolean mock;
    private final String fromAddress;

    /**
     * Creates the email service with template and mail dependencies.
     *
     * @param mailSender JavaMail sender
     * @param templateService template renderer
     * @param mock true to log instead of send
     * @param fromAddress sender address
     */
    public EmailNotificationService(JavaMailSender mailSender, EmailTemplateService templateService,
                                    @Value("${notification.mock:true}") boolean mock,
                                    @Value("${notification.from:no-reply@smartshop.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateService = templateService;
        this.mock = mock;
        this.fromAddress = fromAddress;
    }

    /**
     * Sends an order confirmation message.
     *
     * @param orderId confirmed order id
     * @param userId buyer id
     */
    @Override
    public void sendOrderConfirmed(UUID orderId, UUID userId) {
        String html = templateService.render("order-confirmed", Map.of("orderId", orderId, "userId", userId));
        send("customer-" + userId + "@example.com", "Your SmartShop order is confirmed", html);
    }

    /**
     * Sends an order cancellation message.
     *
     * @param orderId cancelled order id
     * @param userId buyer id
     * @param reason cancellation reason
     */
    @Override
    public void sendOrderCancelled(UUID orderId, UUID userId, String reason) {
        String html = templateService.render("order-confirmed", Map.of("orderId", orderId, "userId", userId, "reason", reason));
        send("customer-" + userId + "@example.com", "Your SmartShop order was cancelled", html);
    }

    /**
     * Sends a welcome message to a newly registered user.
     *
     * @param email recipient email
     * @param firstName recipient first name
     */
    @Override
    public void sendWelcome(String email, String firstName) {
        String html = templateService.render("welcome", Map.of("firstName", firstName));
        send(email, "Welcome to SmartShop", html);
    }

    /**
     * Sends or logs an HTML email.
     *
     * @param to recipient email
     * @param subject email subject
     * @param html rendered HTML body
     */
    private void send(String to, String subject, String html) {
        if (mock) {
            log.info("Mock email to={} subject={} body={}", to, subject, html);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Unable to send notification email", ex);
        }
    }
}
