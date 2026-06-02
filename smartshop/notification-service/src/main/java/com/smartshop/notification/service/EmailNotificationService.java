package com.smartshop.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * EmailNotificationService - HTML Email Delivery via JavaMail and Thymeleaf Templates
 *
 * <h2>Purpose</h2>
 * Handles the technical details of composing and sending HTML emails.
 * Uses Thymeleaf templates for consistent, maintainable email design.
 * Business event consumers (OrderEventConsumer) call this service
 * with prepared template data.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Thymeleaf Templates: HTML files with ${variable} placeholders.
 *       At render time, variables are replaced with actual values.
 *       This separates email CONTENT from email SENDING logic.</li>
 *   <li>MIME Messages: For HTML emails, we use MimeMessage (not SimpleMailMessage).
 *       MIME (Multipurpose Internet Mail Extensions) supports HTML content,
 *       inline images, and file attachments.</li>
 *   <li>MimeMessageHelper: Spring's helper for building MIME messages.
 *       multipart=true: allows both HTML and plain-text alternative.
 *       (Good email practice: include plain-text for email clients that block HTML)</li>
 *   <li>Why not send directly from Kafka consumer?
 *       Separating email sending into this service allows:
 *       1. Easy testing — mock EmailNotificationService in consumer tests
 *       2. Reuse — multiple consumers can send emails via the same service
 *       3. Template management — all template rendering is centralized</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${notification.email.from:noreply@smartshop.com}")
    private String fromEmail;

    @Value("${notification.email.from-name:SmartShop}")
    private String fromName;

    /**
     * Sends an HTML email using a Thymeleaf template.
     *
     * @param to           recipient email address
     * @param subject      email subject line
     * @param templateName the Thymeleaf template file name (without .html extension)
     * @param variables    template variables to substitute in the template
     */
    public void sendTemplatedEmail(String to, String subject,
                                    String templateName, Map<String, Object> variables) {
        log.info("Sending email: to={} template={} subject={}", to, templateName, subject);

        try {
            // Build Thymeleaf context with template variables
            Context context = new Context();
            context.setVariables(variables);

            // Render the HTML template to a string
            // Thymeleaf replaces ${variable} placeholders with actual values
            String htmlContent = templateEngine.process(templateName, context);

            // Build MIME message
            MimeMessage message = mailSender.createMimeMessage();
            // multipart=true: allows HTML content; charset=UTF-8: supports international characters
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            // setHtml=true: marks content as HTML (not plain text)
            helper.setText(htmlContent, true);

            // Actually send the email via the configured SMTP server
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            // Rethrow to trigger Kafka retry mechanism
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Sends a simple plain-text email (no template, no HTML).
     * Used for system alerts and simple notifications.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param text    plain text body
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
