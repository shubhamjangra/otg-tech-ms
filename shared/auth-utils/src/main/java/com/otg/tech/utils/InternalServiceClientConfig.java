package com.otg.tech.utils;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.Objects;

import static com.otg.tech.constant.CommonConstant.NO_AES_KEY;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InternalServiceClientConfig {

    @Value("${aes.encryption.noauth:test}")
    private String noAuth;

    @Bean("InternalServiceClientConfiguration")
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            requestTemplate.header(NO_AES_KEY, noAuth);

            if (Objects.nonNull(requestAttributes)) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                requestTemplate.header("X-Trace-ID", MDC.get("traceId"));
                requestTemplate.header("Authorization", request.getHeader("Authorization"));
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String originalHeader = headerNames.nextElement();
                    String cleanedHeader = originalHeader.toLowerCase().trim();
                    if (cleanedHeader.startsWith("x-")) {
                        requestTemplate.header(originalHeader, request.getHeader(originalHeader));
                    }
                }
            }
        };
    }
}
