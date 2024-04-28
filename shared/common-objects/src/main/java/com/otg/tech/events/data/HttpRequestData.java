package com.otg.tech.events.data;

import lombok.Builder;

@Builder
public record HttpRequestData(String requestId,
                              String clientIpAddress,
                              String requestUri,
                              Object request,
                              Object response,
                              String errorMessage,
                              String traceId,
                              String podId) {

    public static HttpRequestData empty() {
        return new HttpRequestData(null, null, null, null,
                null, null, null, null);
    }
}
