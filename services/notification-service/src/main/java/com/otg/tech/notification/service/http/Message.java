package com.otg.tech.notification.service.http;

import java.util.UUID;

public record Message(String templateCode, String subject, String body) {

    public String id() {
        return UUID.randomUUID().toString();
    }
}
