package com.otg.tech.aspect;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

public class DecryptHttpInputMessage implements HttpInputMessage {

    private final HttpHeaders headers;
    private final InputStream body;

    public DecryptHttpInputMessage(HttpHeaders headers, InputStream body) {
        super();
        this.headers = headers;
        this.body = body;
    }

    @Override
    public InputStream getBody() {
        return this.body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}
