package com.otg.tech.events.data;

import lombok.Builder;

import java.util.Map;

public record AuditData(HttpRequestData request,
                        CustomerData customer,
                        Map<String, Object> extraData) {

    @Builder
    public AuditData {
    }

    public HttpRequestData request() {
        return this.request == null
                ? HttpRequestData.empty()
                : this.request;
    }
}
