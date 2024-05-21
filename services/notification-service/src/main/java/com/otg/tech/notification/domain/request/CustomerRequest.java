package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
        @JsonProperty("userId") String userId,
        @JsonProperty("mobileNo") String mobileNo,
        @Email @JsonProperty("email") String email,
        @NotBlank @JsonProperty("language") String language,
        @Schema(example = "2023-08-20 12:58:08")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} (0[0-9]|1[0-9]|2[0-3]):(0[0-9]|[1-5][0-9]):(0[0-9]|[1-5][0-9])",
                message = "Invalid date and time format. Expected: yyyy-MM-dd HH:mm:ss")
        String scheduledDate) {
}
