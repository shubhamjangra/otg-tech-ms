package com.otg.tech.auditingutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;

@Component
@Aspect
@Slf4j(topic = "audit-logs")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class LoggingAspect {

    @Autowired
    ObjectMapper objectMapper;

    @Around("(execution(* com.otg.tech.*.controller.*.*(..)) && @annotation(postMapping))")
    @SuppressWarnings("unused")
    public Object auditPostEndpoints(ProceedingJoinPoint proceedingJoinPoint, PostMapping postMapping)
            throws Throwable {
        long start = System.currentTimeMillis();
        Object[] requestArgs = proceedingJoinPoint.getArgs();
        String methodName = proceedingJoinPoint.getSignature().toShortString();
        String requestUri = MDC.get("requestUri");
        log.info("Request for endpoint {} and controller method {} is {}",
                requestUri,
                methodName,
                Arrays.asList(requestArgs));
        String responseJson = null;
        try {
            var response = proceedingJoinPoint.proceed();
            responseJson = objectMapper.writeValueAsString(response);
            return response;
        } catch (ApplicationException e) {
            log.error("Application Exception Error Details : statusCode : {} - errorCode : {} - message : {}",
                    e.getCode(), e.getErrorCode(), e.getErrorMessage());
            responseJson = e.getMessage();
            log.error("Application Exception while executing the method {}."
                            + " Exception message is {}",
                    methodName, responseJson, e);
            throw e;
        } catch (Exception e) {
            responseJson = e.getMessage();
            log.error("Exception while executing the method {}."
                            + " Exception message is {}",
                    methodName, responseJson, e);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("Response for endpoint {} and controller method {} is {}, timeTaken {} ms",
                    requestUri,
                    methodName,
                    responseJson, executionTime);
        }
    }
}
