package com.otg.tech.redis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Session implements Serializable {

    private String accessToken;
    private String refreshToken;
    private UserProfile userProfile;

    public Session(String accessToken, String refreshToken, UserProfile userProfile) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userProfile = userProfile;
    }

    public Session(String accessToken, UserProfile userProfile) {
        this.accessToken = accessToken;
        this.userProfile = userProfile;
    }
}
