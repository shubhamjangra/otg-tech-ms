package com.otg.tech.notification.controller;

import com.otg.tech.annotation.ActivityAudit;
import com.otg.tech.commons.ApiResponse;
import com.otg.tech.notification.domain.request.GetProviderRequest;
import com.otg.tech.notification.domain.request.ProviderConfigRequest;
import com.otg.tech.notification.domain.request.ProviderRequest;
import com.otg.tech.notification.domain.response.ProviderConfigResponse;
import com.otg.tech.notification.domain.response.ProviderListResponse;
import com.otg.tech.notification.domain.response.ProviderResponse;
import com.otg.tech.notification.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/providers")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/create")
    @ActivityAudit(auditType = "Create provider",
            message = "create provider from notification service.")
    public ApiResponse<ProviderResponse> createProvider(@Valid @RequestBody ProviderRequest providerRequest) {
        return ApiResponse.success(this.providerService.createProvider(providerRequest));
    }

    @PostMapping("/get")
    @ActivityAudit(auditType = "Get provider",
            message = "Get provider from notification service.")
    public ApiResponse<ProviderResponse> getProvider(
            @Valid @RequestBody GetProviderRequest getProviderRequest) {
        return ApiResponse.success(this.providerService.getProvider(getProviderRequest));
    }

    @PostMapping("/list")
    @ActivityAudit(auditType = "Provider list",
            message = "Provider list from notification service.")
    public ApiResponse<List<ProviderListResponse>> listProviders() {
        return ApiResponse.success(this.providerService.listProviders());
    }

    @PostMapping("/create/config")
    @ActivityAudit(auditType = "Add Provider config",
            message = "Add Provider config from notification service.")
    public ApiResponse<List<ProviderConfigResponse>> addProviderConfig(
            @Valid @RequestBody ProviderConfigRequest providerConfigRequest) {
        return ApiResponse.success(this.providerService.addProviderConfig(providerConfigRequest));
    }
}
