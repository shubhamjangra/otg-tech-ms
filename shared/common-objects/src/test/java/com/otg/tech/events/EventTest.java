package com.otg.tech.events;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void should_create_an_event() {
        Event<Map<String, String>> event = Event.<Map<String, String>>builder()
                .source("/api/v1/customers")
                .type("com.otg.tech.customer.registered")
                .data(Map.of("name", "test", "age", "100"))
                .build();

        assertThat(event.id()).isNotNull();
        assertThat(event.time()).isNotNull();
        assertThat(event.source()).isEqualTo("/api/v1/customers");
        assertThat(event.type()).isEqualTo("com.otg.tech.customer.registered");
        assertThat(event.data()).isEqualTo(Map.of("name", "test", "age", "100"));
    }
}
