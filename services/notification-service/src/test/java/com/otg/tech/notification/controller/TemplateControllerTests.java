package com.otg.tech.notification.controller;

import com.otg.tech.notification.AbstractIntegrationTest;
import com.otg.tech.notification.domain.entity.Template;
import com.otg.tech.notification.domain.request.CreateTemplateRequest;
import com.otg.tech.notification.domain.request.UpdateTemplateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1003;
import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1004;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TemplateControllerTests extends AbstractIntegrationTest {

    @AfterEach
    void tearDown() {
        this.templateRepository.deleteAll();
    }

    @Test
    void should_create_template_when_valid_data_is_given() throws Exception {
        CreateTemplateRequest createTemplateRequest
                = new CreateTemplateRequest("Test Template", "English", "Test Subject", "Test Body Template");
        String content = objectMapper.writeValueAsString(createTemplateRequest);
        this.mockMvc.perform(post("/api/notification-service/templates/create").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_not_create_template_when_valid_data_is_not_given() throws Exception {
        Template template = Template.builder().templateCode("Test Template").subject("Test Subject").language("English")
                .bodyTemplate("Test Body template".getBytes()).build();
        templateRepository.save(template);
        CreateTemplateRequest createTemplateRequest
                = new CreateTemplateRequest("Test Template", "English", "Test Subject", "Test Body Template");
        String content = objectMapper.writeValueAsString(createTemplateRequest);
        this.mockMvc.perform(post("/api/notification-service/templates/create").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.statusCode").value(NOTIFY1003.getHttpStatus()))
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    void should_update_template_when_valid_data_is_given() throws Exception {
        Template template = Template.builder().templateCode("Test Template").subject("Test Subject").language("English")
                .bodyTemplate("Test Body template".getBytes()).build();
        Template response = templateRepository.save(template);
        UpdateTemplateRequest updateTemplateRequest
                = new UpdateTemplateRequest(response.getId(), "Test Template", "English", "Test Subject Update",
                "Test Body Template Update");
        String content = objectMapper.writeValueAsString(updateTemplateRequest);
        this.mockMvc.perform(post("/api/notification-service/templates/update").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_not_update_template_when_valid_data_is_not_given() throws Exception {
        Template template = Template.builder().templateCode("Test Template").subject("Test Subject").language("English")
                .bodyTemplate("Test Body template".getBytes()).build();
        templateRepository.save(template);
        UpdateTemplateRequest updateTemplateRequest
                = new UpdateTemplateRequest("1234", "Test Template", "English", "Test Subject Update",
                "Test Body Template Update");
        String content = objectMapper.writeValueAsString(updateTemplateRequest);
        this.mockMvc.perform(post("/api/notification-service/templates/update").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.statusCode").value(NOTIFY1004.getHttpStatus()))
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    void should_get_template_when_valid_data_is_given() throws Exception {
        Template template = Template.builder().templateCode("Test Template").subject("Test Subject").language("English")
                .bodyTemplate("Test Body template".getBytes()).build();
        templateRepository.save(template);
        this.mockMvc.perform(post("/api/notification-service/templates/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_not_get_template_when_valid_data_is_given() throws Exception {
        this.mockMvc.perform(post("/api/notification-service/templates/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
