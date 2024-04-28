package com.otg.tech.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.otg.tech.events.enums.EventActions;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;

import static com.otg.tech.util.commons.Utils.deserialize;
import static com.otg.tech.util.commons.Utils.toJavaPojo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder
@JsonSerialize
@SuppressWarnings("java:S119")
public record Event<DATA>(
        String id,
        String source,
        OffsetDateTime time,
        String type,
        EventActions action,
        DATA data
) {

    @Builder
    public Event(
            String source,
            String type,
            EventActions action,
            DATA data) {
        this(
                UUID.randomUUID().toString(),
                source,
                OffsetDateTime.now(),
                type,
                action,
                data
        );
    }

    @SuppressWarnings("unused")
    public DATA typedData(Class<DATA> dataClass) {
        if (this.data instanceof LinkedHashMap<?, ?> linkedHashMap) {
            return deserialize(linkedHashMap, dataClass);
        } else if (this.data instanceof String strJson) {
            return toJavaPojo(strJson, dataClass);
        } else
            return this.data;
    }
}
