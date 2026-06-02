package com.smartshop.notification.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * EmailTemplateService - Renders HTML email bodies from Thymeleaf templates.
 *
 * <h2>Purpose</h2>
 * Separates "what the email looks like" (templates) from "when to send it"
 * (consumers). Designers can edit the HTML templates without touching Java.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Thymeleaf</b>: a server-side template engine. We bind variables into
 *       a {@link Context} and it produces the final HTML string.</li>
 *   <li>Template names map to files under {@code src/main/resources/templates}.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by {@code EmailNotificationService} to build message bodies before sending.
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Renders a template with the given variables into an HTML string.
     *
     * @param templateName the template file name (without .html), e.g. "welcome"
     * @param variables    the model variables referenced in the template
     * @return the rendered HTML body
     */
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        // Expose all provided variables to the template's ${...} expressions.
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
