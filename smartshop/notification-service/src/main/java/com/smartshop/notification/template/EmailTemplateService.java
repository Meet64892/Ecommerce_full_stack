package com.smartshop.notification.template;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * EmailTemplateService - Thymeleaf email rendering helper.
 *
 * <h2>Purpose</h2>
 * Centralizes template rendering logic to keep notification transport class concise.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Template engine: separates presentation from business logic.</li>
 *   <li>Parameterized rendering: dynamic variables injected per event.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * EmailNotificationService uses this class to build HTML bodies.
 *
 * @see com.smartshop.notification.service.EmailNotificationService
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    /**
     * Renders a template with variables.
     *
     * @param templateName template file name without extension
     * @param variables template variables
     * @return rendered HTML
     */
    public String render(final String templateName, final Map<String, Object> variables) {
        final Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
