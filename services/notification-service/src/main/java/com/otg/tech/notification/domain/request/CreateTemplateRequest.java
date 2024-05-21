package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateTemplateRequest(
        @NotBlank @JsonProperty("templateCode") String templateCode,
        @NotBlank @JsonProperty("language") String language,
        @NotBlank @JsonProperty("subject") String subject,
        @NotBlank @JsonProperty("bodyTemplate") String bodyTemplate) {
}
