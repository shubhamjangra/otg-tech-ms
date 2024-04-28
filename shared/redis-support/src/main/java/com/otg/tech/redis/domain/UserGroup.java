package com.otg.tech.redis.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonSerialize
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroup implements Serializable {

    private String id;
    private String name;

    @JsonCreator
    @SuppressWarnings("unused")
    public UserGroup(@JsonProperty("id") String id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
