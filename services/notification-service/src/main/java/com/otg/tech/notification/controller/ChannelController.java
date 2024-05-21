package com.otg.tech.notification.controller;

import com.otg.tech.annotation.ActivityAudit;
import com.otg.tech.commons.ApiResponse;
import com.otg.tech.notification.domain.request.ChannelRequest;
import com.otg.tech.notification.domain.response.ChannelResponse;
import com.otg.tech.notification.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/channels")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/create")
    @ActivityAudit(auditType = "Create channel",
            message = "create channel from notification service.")
    public ApiResponse<ChannelResponse> createChannel(@Valid @RequestBody ChannelRequest channelRequest) {
        return ApiResponse.success(this.channelService.createChannel(channelRequest));
    }

    @PostMapping("/list")
    @ActivityAudit(auditType = "channel list",
            message = "Return channel list from notification service.")
    public ApiResponse<List<ChannelResponse>> listChannels() {
        return ApiResponse.success(this.channelService.listChannels());
    }
}
