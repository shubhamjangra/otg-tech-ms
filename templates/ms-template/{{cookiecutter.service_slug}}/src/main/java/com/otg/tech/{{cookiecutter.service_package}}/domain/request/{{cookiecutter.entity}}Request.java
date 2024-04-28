package com.otg.tech.

{{cookiecutter.service_package}}.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public record {
    {
        cookiecutter.entity
    }
}

Request(
        @NotBlank String title,
        @JsonProperty("is_active") boolean isActive) {
}