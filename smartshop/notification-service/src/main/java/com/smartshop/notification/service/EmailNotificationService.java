package com.smartshop.notification.service;

import com.smartshop.notification.template.EmailTemplateService;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * EmailNotificationService - Email channel implementation.
 *
 * <h2>Purpose</h2>
 * Sends transactional emails from domain events. In local development where SMTP may be absent,
 * it logs delivery intent instead of failing critical event consumption.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Best-effort side effects: notification failure should not rollback order state.</li>
 *   <li>Template-driven messaging: consistent branded output.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Kafka consumers invoke this service for welcome/order event notifications.
 *
 * @see NotificationService
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    /**
     * Sends welcome email.
     *
     * @param email recipient email
     * @param firstName first name
     */
    @Override
    public void sendWelcomeEmail(final String email, final String firstName) {
        sendHtml(email, "Welcome to SmartShop", "welcome", Map.of("firstName", firstName));
    }

    /**
     * Sends order confirmation email.
     *
     * @param email recipient email
     * @param orderId order id
     */
    @Override
    public void sendOrderConfirmedEmail(final String email, final Long orderId) {
        sendHtml(email, "Order Confirmed", "order-confirmed", Map.of("orderId", orderId));
    }

    /**
     * Sends order cancellation email.
     *
     * @param email recipient email
     * @param orderId order id
     * @param reason reason
     */
    @Override
    public void sendOrderCancelledEmail(final String email, final Long orderId, final String reason) {
        sendHtml(email, "Order Cancelled", "order-confirmed", Map.of("orderId", orderId, "reason", reason));
    }

    /**
     * Sends HTML email using Thymeleaf template.
     *
     * @param to recipient email
     * @param subject email subject
     * @param template template name
     * @param vars template variables
     */
    private void sendHtml(final String to, final String subject, final String template, final Map<String, Object> vars) {
        try {
            final String html = templateService.render(template, vars);
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {} for subject {}", to, subject);
        } catch (Exception ex) {
            // In event-driven systems, notification retries/DLT are preferable to blocking source flow.
            log.warn("Email delivery failed for {} with subject {}. Falling back to log-only mode.", to, subject, ex);
        }
    }
}
