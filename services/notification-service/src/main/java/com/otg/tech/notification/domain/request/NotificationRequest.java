package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record NotificationRequest(
        @NotBlank @JsonProperty("eventType") String eventType,
        @JsonProperty("idempotencyKey") String idempotencyKey,
        @NotNull @Valid @JsonProperty("customerRequest") CustomerRequest customerRequest,
        @JsonProperty("data") Map<String, Object> data) {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Schema(hidden = true)
    @JsonIgnore
    public String getExistingOrCreateIdempotencyKey() {
        return idempotencyKey == null || idempotencyKey.trim().isEmpty()
                ? UUID.randomUUID().toString()
                : idempotencyKey;
    }

    @Schema(hidden = true)
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCustomerAsMap() {
        return MAPPER.convertValue(this.customerRequest, Map.class);
    }
}