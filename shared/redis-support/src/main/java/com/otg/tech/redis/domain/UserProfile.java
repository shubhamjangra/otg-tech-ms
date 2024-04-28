package com.otg.tech.redis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {

    Map<String, Merchant> merchants;
    private String userId;
    private String name;
    private String username;
    private String emailAddress;
    private String mobileNumber;
    private String cifNumber;
    private UserType userType;
    private String preferredLanguage;
    private String employeeId;
    private String branchName;
    private String branchCode;
    private Platform platform;
    private String activeMerchantId;
    private List<Role> roles;
    private List<String> permissions;
    private List<UserGroup> userGroups;
    private DeviceInfo deviceInfo;
    private BrowserInfo browserInfo;
    private String lastLoggedIn;
    private boolean isSelfAuth;
    private UserNotification userNotification;
    private String persona;
    private String theme;
}

