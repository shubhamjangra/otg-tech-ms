Event Publisher
-----

Event publisher is a library that exposing an API to publish message to an event broker. The event broker we support is
Kafka.

## Adding Event Publisher to your service

You have to add Event Publisher library to `settings.gradle` and `build.gradle` as shown below.

Add following to `settings.gradle`

```
includeBuild '../../shared/event-publisher'
```

Add following to `build.gradle` dependencies section.

```
implementation "com.otg.tech:event-publisher"
```

Add below properties in application.properties to enable kafka

```
## Kafka based Event publisher support
kafka.event.publisher.enabled=true
```

Also Add below in Docker file

RUN cp /usr/lib/jvm/zulu17-ca/lib/security/cacerts /tmp/kafka.client.truststore.jks

## Event Publisher Usage

There are two main classes - `Event` and `EventPublisher`.

EventPublisher bean is automatically created when you add library to the classpath.

```java

@Autowired
private EventPublisher eventPublisher;
@Value("service.kafka.topic")
private String topic;

public void doSth() {
    Event<Map<String, Object>> event = Event.<Map<String, Object>>builder()
            .type("com.otg.tech.initservice.blocked_device_made_request")
            .source("init-journey-service")
            .data(Map.of("device_id", deviceId, "app_id", appId, "status_code", LOCKED))
            .build();
    eventPublisher.publish(topic, event);
}
```

For each event id and timestamp are added automatically.