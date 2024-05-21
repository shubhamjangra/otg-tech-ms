package com.otg.tech.notification.controller;

import com.otg.tech.notification.AbstractIntegrationTest;
import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.enums.ChannelType;
import com.otg.tech.notification.domain.request.ChannelRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChannelControllerTests extends AbstractIntegrationTest {

    @AfterEach
    void tearDown() {
        channelRepository.deleteAll();
    }

    @Test
    void should_create_channel_when_valid_data_is_given() throws Exception {
        ChannelRequest channelRequest = new ChannelRequest(ChannelType.PUSH_NOTIFICATION);
        String content = objectMapper.writeValueAsString(channelRequest);
        this.mockMvc.perform(post("/api/notification-service/channels/create").content(content)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_channel_when_valid_data_is_given() throws Exception {
        Channel channel = Channel.builder().channelType(ChannelType.PUSH_NOTIFICATION).build();
        channelRepository.save(channel);
        this.mockMvc.perform(post("/api/notification-service/channels/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void should_get_empty_list_for_get_channel_when_invalid_data_is_given() throws Exception {
        this.mockMvc.perform(post("/api/notification-service/channels/list")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
