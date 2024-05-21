package com.otg.tech.notification.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.entity.Template;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.nio.charset.StandardCharsets;

@Builder
public record TemplateResponse(
        @NotBlank @JsonProperty("id") String id,
        @NotBlank @JsonProperty("templateCode") String templateCode,
        @NotBlank @JsonProperty("language") String language,
        @NotBlank @JsonProperty("subject") String subject,
        @NotBlank @JsonProperty("bodyTemplate") String bodyTemplate) {

    public static TemplateResponse toTemplate(Template template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .language(template.getLanguage())
                .subject(template.getSubject())
                .bodyTemplate(new String(template.getBodyTemplate(), StandardCharsets.UTF_8))
                .build();
    }
}
