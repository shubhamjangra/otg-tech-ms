package com.otg.tech.auditingutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.auditingutils.model.AuditingModel;
import com.otg.tech.eventpublisher.EventPublisher;
import com.otg.tech.events.AppTopic;
import com.otg.tech.events.Event;
import com.otg.tech.test.controller.TestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditingAspectTests {

    private StubEventPublisher eventPublisher;
    private TestController controllerProxy;

    @BeforeEach
    void setUp() {
        eventPublisher = new StubEventPublisher();
        AuditingAspect auditingAspect = new AuditingAspect();
        auditingAspect.objectMapper = new ObjectMapper();
        auditingAspect.serviceName = "test-service";
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new TestController());
        aspectJProxyFactory.addAspect(auditingAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controllerProxy = (TestController) aopProxy.getProxy();
    }

    @Test
    void should_execute_aspect_on_success() {
        controllerProxy.doSth(Map.of("request", "test-request"));
        AuditingModel auditingModel = getAuditingModel();
        Event<AuditingModel> auditEvent = Event.<AuditingModel>builder()
                .source("test-service").type("com.otg.tech.auditing.http_request").data(auditingModel).build();
        eventPublisher.publish("audit-events", "testkey", auditEvent);
        var events = eventPublisher.getEvents();
        assertThat(events).hasSize(1);
        StubEventPublisher.TestEvent testEventRecord = events.get(0);
        assertThat(testEventRecord.key()).isEqualTo("testkey");
        assertThat(testEventRecord.topic()).isEqualTo(AppTopic.AUDIT_EVENTS.getTopicName());
        Event<?> event = testEventRecord.event();
        AuditingModel model = (AuditingModel) event.data();
        assertThat(event.source()).isEqualTo("test-service");
        assertThat(event.type()).isEqualTo("com.otg.tech.auditing.http_request");
        assertThat(event.source()).isEqualTo("test-service");
        assertThat(model.request()).isEqualTo("test-request");
        assertThat(model.response()).isEqualTo("test-response");
    }

    private AuditingModel getAuditingModel() {
        return AuditingModel.builder().auditType("test").ipAddress("454.353.535.355").emailAddress("test@test.com")
                .correlationId("test").persona("test").channel("test").deviceId("tests").message("tst")
                .mobileNumber("534535323").sessionId("ffdgndfr645646gddgd").request("test-request")
                .response("test-response").build();
    }

    @Test
    void should_execute_aspect_on_exception() {
        try {
            controllerProxy.doSthOnException(Map.of("request", "test-request"));
        } catch (Exception e) {
            assertThat(e).hasMessage("testing for exception");
        }
        AuditingModel auditingModel = getAuditingModel();

        Event<AuditingModel> auditEvent = Event.<AuditingModel>builder()
                .source("test-service").type("com.otg.tech.auditing.http_request").data(auditingModel).build();
        eventPublisher.publish("audit-events", null, auditEvent);

        var events = eventPublisher.getEvents();
        assertThat(events).hasSize(1);
        StubEventPublisher.TestEvent testEventRecord = events.get(0);
        assertThat(testEventRecord.key()).isEqualTo(null);
        assertThat(testEventRecord.topic()).isEqualTo(AppTopic.AUDIT_EVENTS.getTopicName());
        Event<?> event = testEventRecord.event();
        AuditingModel model = (AuditingModel) event.data();
        assertThat(event.source()).isEqualTo("test-service");
        assertThat(event.type()).isEqualTo("com.otg.tech.auditing.http_request");
        assertThat(event.source()).isEqualTo("test-service");
        assertThat(model.request()).isEqualTo("test-request");
    }
}

class StubEventPublisher implements EventPublisher {

    private final List<TestEvent> events = new ArrayList<>();

    @Override
    public <EventData> void publish(String topic, String key, Event<EventData> event) {
        events.add(new TestEvent(topic, key, event));
    }

    public List<TestEvent> getEvents() {
        return events;
    }

    public record TestEvent(String topic, String key, Event<?> event) {
    }
}
