package com.smartshop.notification.template;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * EmailTemplateService - Renders Thymeleaf HTML email templates.
 *
 * <h2>Purpose</h2>
 * Templates keep message layout separate from Java code so copy and branding can change without rewriting business
 * logic. Thymeleaf is used because it integrates directly with Spring Boot and supports HTML templates.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Template context: Variables are provided to the template engine by name.</li>
 *   <li>Separation of concerns: Rendering is independent from Kafka and email delivery.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * EmailNotificationService calls this service before sending or logging emails.
 *
 * @see com.smartshop.notification.service.EmailNotificationService
 * @author SmartShop Team
 */
@Service
public class EmailTemplateService {
    private final TemplateEngine templateEngine;

    /**
     * Creates the template service with the Thymeleaf engine.
     *
     * @param templateEngine configured Thymeleaf template engine
     */
    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Renders a template with named variables.
     *
     * @param templateName template file name without .html
     * @param variables variables used by the template
     * @return rendered HTML text
     */
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
