package com.otg.tech.auditingutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.annotation.ActivityAudit;
import com.otg.tech.auditingutils.model.AuditingModel;
import com.otg.tech.auditingutils.service.AuditExchangeService;
import com.otg.tech.events.Event;
import com.otg.tech.events.data.CustomerData;
import com.otg.tech.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class AuditingAspect extends ExceptionHandlerAuditingSupport {

    @Autowired
    public ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    public String serviceName;
    @Autowired
    AuditExchangeService auditExchangeService;

    @Around("@annotation(com.otg.tech.annotation.ActivityAudit)")
    @SuppressWarnings("unused")
    public Object auditPostEndpoints(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        // retrieve the passed parameter in annotation
        ActivityAudit activityAudit = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod()
                .getAnnotation(ActivityAudit.class);

        Object[] requestArgs = proceedingJoinPoint.getArgs();
        String methodName = proceedingJoinPoint.getSignature().toShortString();
        Event.EventBuilder<AuditingModel> eventBuilder = Event.builder();
        AuditingModel.AuditingModelBuilder auditDataBuilder = AuditingModel.builder();
        eventBuilder.type("com.otg.tech.auditing.http_request").source(serviceName);
        String requestUri = MDC.get("requestUri");
        log.info("Request for endpoint {} and controller method {} is {}", requestUri, methodName,
                Arrays.asList(requestArgs));
        String clientIpAddress = MDC.get("clientIpAddress");
        String requestId = MDC.get("correlationId");
        String sessionId = MDC.get("sessionId");
        String userId = MDC.get("userId");
        CustomerData customer = getCustomerData();
        Object requestBody = requestArgs.length == 0 ? null : requestArgs[0];
        String responseJson = null;
        String requestJson;
        try {
            var response = proceedingJoinPoint.proceed();
            requestJson = objectMapper.writeValueAsString(requestBody);
            responseJson = objectMapper.writeValueAsString(response);
            auditDataBuilder
                    .auditType(activityAudit.auditType())
                    .sessionId(sessionId)
                    .userId(userId)
                    .ipAddress(clientIpAddress)
                    .correlationId(requestId)
                    .deviceId(customer.deviceId())
                    .mobileNumber(customer.mobileNo())
                    .persona(customer.persona())
                    .emailAddress("")
                    .request(requestJson)
                    .response(responseJson)
                    .message(activityAudit.message())
                    .channel(serviceName);
            return response;
        } catch (ApplicationException e) {
            log.error("Application Exception Error Details : statusCode : {} - errorCode : {} - message : {}",
                    e.getCode(), e.getErrorCode(), e.getErrorMessage());
            responseJson = e.getMessage();
            log.error("Application Exception while executing the method {}." + " Exception message is {}", methodName,
                    responseJson, e);
            eventBuilder.data(auditDataBuilder.build());
            throw e;
        } catch (Exception e) {
            responseJson = e.getMessage();
            log.error("Exception while executing the method {}." + " Exception message is {}", methodName, responseJson,
                    e);
            eventBuilder.data(auditDataBuilder.build());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("Response for endpoint {} and controller method {} is {}, timeTaken {} ms", requestUri, methodName,
                    responseJson, executionTime);
            eventBuilder.data(auditDataBuilder.build());
            auditExchangeService.sendAuditingEvent(eventBuilder.build(), customer.customerId());
        }
    }
}
