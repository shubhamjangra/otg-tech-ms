package com.otg.tech.auditingutils;

import com.otg.tech.auditingutils.model.AuditingModel;
import com.otg.tech.auditingutils.service.AuditExchangeService;
import com.otg.tech.events.Event;
import com.otg.tech.events.data.CustomerData;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
public class ExceptionHandlerAuditingSupport {
    @Autowired
    AuditExchangeService auditExchangeService;
    @Value("${spring.application.name}")
    String serviceName;

    @SuppressWarnings("unused")
    public void handleMethodArgumentNotValidException(Object requestBody, MethodArgumentNotValidException mae) {
        Event.EventBuilder<AuditingModel> eventBuilder = Event.builder();
        AuditingModel.AuditingModelBuilder auditDataBuilder = AuditingModel.builder();
        eventBuilder.type("com.otg.tech.auditing.http_request")
                .source(serviceName);
        String clientIpAddress = MDC.get("clientIpAddress");
        String requestId = MDC.get("correlationId");
        CustomerData customer = getCustomerData();
        eventBuilder.data(AuditingModel.buildAuditingModel(customer.customerId(), customer.sessionId(), "",
                clientIpAddress, requestId, customer.deviceId(), customer.mobileNo(), customer.persona(), "",
                "", "", "", ""));
        auditExchangeService.sendAuditingEvent(eventBuilder.build(), customer.customerId());
    }

    CustomerData getCustomerData() {
        String customerId = MDC.get("customerId");
        String deviceId = MDC.get("deviceId");
        String device = MDC.get("device");
        String mobileNo = MDC.get("mobileNo");
        String persona = MDC.get("persona");
        String sessionId = MDC.get("sessionId");
        String eventId = MDC.get("eventId");
        return CustomerData.builder()
                .customerId(customerId)
                .device(device)
                .deviceId(deviceId)
                .mobileNo(mobileNo)
                .persona(persona)
                .sessionId(sessionId)
                .eventId(eventId)
                .build();
    }
}
