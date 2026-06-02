package com.smartshop.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailNotificationService - Email implementation of {@link NotificationService}.
 *
 * <h2>Purpose</h2>
 * Sends (or, in dev, mock-logs) HTML emails. To keep the learning project
 * runnable without a real SMTP server, sending is guarded by a flag: when no
 * mail server is configured we log the email instead of transmitting it.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code JavaMailSender} is injected lazily/optionally; we don't require a
 *       live SMTP server for the demo, demonstrating a "mock" delivery mode.</li>
 *   <li>Logging the rendered email lets you verify the flow end-to-end (place an
 *       order, watch the confirmation "email" appear in this service's logs).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Invoked by the Kafka consumers after a template is rendered.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
public class EmailNotificationService implements NotificationService {

    /** Optional real mail sender; may be null when no SMTP is configured. */
    private final JavaMailSender mailSender;

    /** When false (default in dev), we mock-send by logging instead of SMTP. */
    private final boolean realDeliveryEnabled;

    /** The "from" address stamped on outbound mail. */
    private final String fromAddress;

    /**
     * @param mailSender          optional JavaMailSender (auto-configured if present)
     * @param realDeliveryEnabled whether to actually transmit via SMTP
     * @param fromAddress         the sender address
     */
    public EmailNotificationService(
            @org.springframework.beans.factory.annotation.Autowired(required = false) JavaMailSender mailSender,
            @Value("${smartshop.notification.real-delivery:false}") boolean realDeliveryEnabled,
            @Value("${smartshop.notification.from-address:no-reply@smartshop.dev}") String fromAddress) {
        this.mailSender = mailSender;
        this.realDeliveryEnabled = realDeliveryEnabled;
        this.fromAddress = fromAddress;
    }

    /** {@inheritDoc} */
    @Override
    public void send(String to, String subject, String htmlBody) {
        // Mock mode (default): log the fully-rendered email so the demo works
        // without external infrastructure.
        if (!realDeliveryEnabled || mailSender == null) {
            log.info("""
                    [MOCK EMAIL]
                    from: {}
                    to: {}
                    subject: {}
                    body:
                    {}""", fromAddress, to, subject, htmlBody);
            return;
        }

        // Real delivery path (enabled by configuration in environments with SMTP).
        try {
            var message = mailSender.createMimeMessage();
            var helper = new org.springframework.mail.javamail.MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true => HTML content.
            mailSender.send(message);
            log.info("Sent email to {} subject='{}'", to, subject);
        } catch (Exception e) {
            // Never let a delivery failure crash event processing; log and move on.
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
