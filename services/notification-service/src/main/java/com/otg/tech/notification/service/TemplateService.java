package com.otg.tech.notification.service;

import com.otg.tech.exception.ApplicationException;
import com.otg.tech.notification.domain.entity.Template;
import com.otg.tech.notification.domain.request.CreateTemplateRequest;
import com.otg.tech.notification.domain.request.UpdateTemplateRequest;
import com.otg.tech.notification.domain.response.TemplateResponse;
import com.otg.tech.notification.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1003;
import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1004;

@Service
@Transactional
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateResponse createTemplate(CreateTemplateRequest createTemplateRequest) {
        this.templateRepository.findByTemplateCode(createTemplateRequest.templateCode())
                .ifPresent(e -> {
                    throw new ApplicationException(NOTIFY1003.getHttpStatus(), NOTIFY1003.getCode(),
                            NOTIFY1003.getMessage());
                });
        Template response = templateRepository.save(new Template(createTemplateRequest.templateCode(),
                createTemplateRequest.language(), createTemplateRequest.subject(),
                createTemplateRequest.bodyTemplate().getBytes()));
        return TemplateResponse.toTemplate(response);
    }

    public TemplateResponse updateTemplate(UpdateTemplateRequest updateTemplateRequest) {
        Template template = this.templateRepository.findById(updateTemplateRequest.id())
                .orElseThrow(() -> new ApplicationException(NOTIFY1004.getHttpStatus(),
                        NOTIFY1004.getCode(), NOTIFY1004.getMessage()));
        template.setTemplateDetails(updateTemplateRequest.templateCode(), updateTemplateRequest.language(),
                updateTemplateRequest.subject(), updateTemplateRequest.bodyTemplate().getBytes());
        Template response = this.templateRepository.save(template);
        return TemplateResponse.toTemplate(response);
    }

    public List<TemplateResponse> listTemplates() {
        List<Template> templateList = this.templateRepository.findAll();
        if (templateList.isEmpty())
            return Collections.emptyList();
        return templateList.stream().map(TemplateResponse::toTemplate).toList();
    }
}
