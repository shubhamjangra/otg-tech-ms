package com.otg.tech.notification.service.http.exceptions;

public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
