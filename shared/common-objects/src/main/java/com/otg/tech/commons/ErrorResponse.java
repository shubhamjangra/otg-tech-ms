package com.otg.tech.commons;

import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.otg.tech.constant.CommonConstant.MDC_CORRELATION_ID;

@SuppressWarnings("unused")
public record ErrorResponse(int statusCode, String errorCode, String message, List<String> errors, String requestId,
                            BigDecimal availableLimit, BigDecimal consumedAmount) {

    public ErrorResponse(int statusCode, String errorCode,
                         String message, List<String> errors) {
        this(statusCode, errorCode, message, errors, MDC.get(MDC_CORRELATION_ID), null, null);
    }

    public ErrorResponse(int code, String message) {
        this(code, null, message, Collections.emptyList(), MDC.get(MDC_CORRELATION_ID), null, null);
    }

    public ErrorResponse(int statusCode, String message, List<String> errors) {
        this(statusCode, null, message, errors, MDC.get(MDC_CORRELATION_ID), null, null);
    }

    public ErrorResponse(int code, String errorCode, String message) {
        this(code, errorCode, message, Collections.emptyList(), MDC.get(MDC_CORRELATION_ID), null, null);
    }

    public ErrorResponse(String errorCode, String message) {
        this(0, errorCode, message, null, MDC.get(MDC_CORRELATION_ID), null, null);
    }

    public ErrorResponse(String errorCode, String message, List<String> errors) {
        this(0, errorCode, message, errors, MDC.get(MDC_CORRELATION_ID), null, null);
    }
}
