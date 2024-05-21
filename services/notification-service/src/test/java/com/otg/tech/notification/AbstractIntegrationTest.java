package com.otg.tech.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.notification.repository.ChannelRepository;
import com.otg.tech.notification.repository.NotificationEventRepository;
import com.otg.tech.notification.repository.NotificationRepository;
import com.otg.tech.notification.repository.ProviderRepository;
import com.otg.tech.notification.repository.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
@TestPropertySource(properties = {"notification.service.scheduling.enable=false"})
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ChannelRepository channelRepository;
    @Autowired
    protected NotificationEventRepository notificationEventRepository;
    @Autowired
    protected NotificationRepository notificationRepository;
    @Autowired
    protected TemplateRepository templateRepository;
    @Autowired
    protected ProviderRepository providerRepository;

    @Test
    void mockMvc_is_not_null() {
        assertThat(mockMvc).isNotNull();
    }
}
