package com.otg.tech.notification.controller;

import com.otg.tech.notification.AbstractIntegrationTest;
import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.entity.Provider;
import com.otg.tech.notification.domain.enums.ChannelType;
import com.otg.tech.notification.domain.enums.ConfigDataType;
import com.otg.tech.notification.domain.request.GetProviderRequest;
import com.otg.tech.notification.domain.request.ProviderConfigRequest;
import com.otg.tech.notification.domain.request.ProviderRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1001;
import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1002;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProviderControllerTests extends AbstractIntegrationTest {

    @AfterEach
    void tearDown() {
        this.providerRepository.deleteAll();
        this.channelRepository.deleteAll();
    }

    @Test
    void should_create_provider_when_valid_data_is_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        ProviderRequest providerRequest = new ProviderRequest("Provider Name", response.getId());
        String content = objectMapper.writeValueAsString(providerRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/create").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_validation_of_channel_for_create_provider_when_valid_data_is_not_given() throws Exception {
        ProviderRequest providerRequest = new ProviderRequest("Provider Name", "1234");
        String content = objectMapper.writeValueAsString(providerRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/create").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.statusCode").value(NOTIFY1001.getHttpStatus()))
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    void should_get_provider_when_valid_data_is_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        Provider provider = new Provider("Provider Name", response);
        Provider response1 = providerRepository.save(provider);
        GetProviderRequest getProviderRequest = new GetProviderRequest(response1.getId());
        String content = objectMapper.writeValueAsString(getProviderRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/get").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_validation_of_provider_for_get_provider_when_valid_data_is_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        Provider provider = new Provider("Provider Name", response);
        providerRepository.save(provider);
        GetProviderRequest getProviderRequest = new GetProviderRequest("1234");
        String content = objectMapper.writeValueAsString(getProviderRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/get").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.statusCode").value(NOTIFY1002.getHttpStatus()))
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    void should_get_all_providers_when_valid_data_is_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        Provider provider = new Provider("Provider Name", response);
        providerRepository.save(provider);
        this.mockMvc.perform(post("/api/notification-service/providers/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_empty_providers_when_valid_data_is_not_present() throws Exception {
        this.mockMvc.perform(post("/api/notification-service/providers/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void should_add_the_provider_config_when_valid_data_is_not_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        Provider provider = new Provider("Provider Name", response);
        Provider provider1 = providerRepository.save(provider);
        ProviderConfigRequest providerConfigRequest =
                new ProviderConfigRequest(provider1.getId(), "Test Key", "Test Value", ConfigDataType.STRING);
        String content = objectMapper.writeValueAsString(providerConfigRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/create/config").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_validation_for_add_the_provider_config_when_valid_data_is_not_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        Channel response = channelRepository.save(channel);
        Provider provider = new Provider("Provider Name", response);
        providerRepository.save(provider);
        ProviderConfigRequest providerConfigRequest =
                new ProviderConfigRequest("1234", "Test Key", "Test Value", ConfigDataType.STRING);
        String content = objectMapper.writeValueAsString(providerConfigRequest);
        this.mockMvc.perform(post("/api/notification-service/providers/create/config").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.statusCode").value(NOTIFY1002.getHttpStatus()))
                .andExpect(jsonPath("$.error.message").exists());
    }
}
