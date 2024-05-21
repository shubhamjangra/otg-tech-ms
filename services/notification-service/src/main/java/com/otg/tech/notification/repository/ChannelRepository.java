package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, String> {
}
