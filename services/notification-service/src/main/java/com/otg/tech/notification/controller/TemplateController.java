package com.otg.tech.notification.controller;

import com.otg.tech.annotation.ActivityAudit;
import com.otg.tech.commons.ApiResponse;
import com.otg.tech.notification.domain.request.CreateTemplateRequest;
import com.otg.tech.notification.domain.request.UpdateTemplateRequest;
import com.otg.tech.notification.domain.response.TemplateResponse;
import com.otg.tech.notification.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/templates")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/create")
    @ActivityAudit(auditType = "Create Template",
            message = "Create Template from notification service.")
    public ApiResponse<TemplateResponse> createTemplate(
            @Valid @RequestBody CreateTemplateRequest createTemplateRequest) {
        return ApiResponse.success(this.templateService.createTemplate(createTemplateRequest));
    }

    @PostMapping("/update")
    @ActivityAudit(auditType = "Update Template",
            message = "Update Template from notification service.")
    public ApiResponse<TemplateResponse> updateTemplate(
            @Valid @RequestBody UpdateTemplateRequest updateTemplateRequest) {
        return ApiResponse.success(this.templateService.updateTemplate(updateTemplateRequest));
    }

    @PostMapping("/list")
    @ActivityAudit(auditType = "Template list",
            message = "Template list from notification service.")
    public ApiResponse<List<TemplateResponse>> listTemplates() {
        return ApiResponse.success(this.templateService.listTemplates());
    }
}
