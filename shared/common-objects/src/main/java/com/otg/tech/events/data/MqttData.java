package com.otg.tech.events.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
public class MqttData implements Serializable {
    CustomerRequest customerRequest;
    Payload payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize
    @Builder
    public static class CustomerRequest implements Serializable {
        String userId;
        String mobileNo;
        String email;
        String language;
        List<String> deviceIds;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize
    @Builder
    public static class Payload implements Serializable {
        String messageType;
        String orderNum;
        String price;
        String type;
    }
}
