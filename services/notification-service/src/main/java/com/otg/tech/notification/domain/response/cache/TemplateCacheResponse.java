package com.otg.tech.notification.domain.response.cache;

import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.entity.Template;
import com.otg.tech.notification.template.TemplateEngine;
import lombok.Builder;

import java.io.IOException;
import java.util.Map;

import static com.otg.tech.notification.constant.NotificationConstants.CUSTOMER;
import static com.otg.tech.notification.constant.NotificationConstants.EVENT;

@Builder
public record TemplateCacheResponse(
        String id,
        String templateCode,
        String language,
        String subject,
        String bodyTemplate) {

    public static TemplateCacheResponse getTemplateCacheResponse(Template template) {
        return TemplateCacheResponse.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .language(template.getLanguage())
                .subject(template.getSubject())
                .bodyTemplate(new String(template.getBodyTemplate()))
                .build();
    }

    public String hydrateTemplate(NotificationEvent event, TemplateEngine templateEngine)
            throws IOException {
        Map<String, Object> data = Map.of(CUSTOMER, event.getCustomerData(),
                EVENT, event.getEventData());
        return templateEngine.execute(this.templateCode(),
                this.bodyTemplate(), data);
    }

    public String hydrateSubject(NotificationEvent event, TemplateEngine templateEngine)
            throws IOException {
        Map<String, Object> data = Map.of(CUSTOMER, event.getCustomerData(),
                EVENT, event.getEventData());
        return templateEngine.execute(this.templateCode(),
                this.subject(), data);
    }
}
