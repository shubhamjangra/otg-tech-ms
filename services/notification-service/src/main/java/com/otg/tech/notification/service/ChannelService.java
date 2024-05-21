package com.otg.tech.notification.service;

import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.request.ChannelRequest;
import com.otg.tech.notification.domain.response.ChannelResponse;
import com.otg.tech.notification.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelResponse createChannel(ChannelRequest channelRequest) {
        Channel channel = new Channel(channelRequest.channelType());
        Channel response = channelRepository.save(channel);
        return ChannelResponse.toChannel(response);
    }

    public List<ChannelResponse> listChannels() {
        List<Channel> channelList = channelRepository.findAll();
        if (channelList.isEmpty())
            return Collections.emptyList();
        return channelList.stream().map(ChannelResponse::toChannel).toList();
    }
}
