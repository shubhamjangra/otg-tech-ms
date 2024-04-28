package com.otg.tech.auth.claims;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuppressWarnings("unused")
public class Identity {
    private String userId;
    private UserType userType;
    private String sessionStateId;
    private List<UserGroup> userGroups;
    private boolean isSelfAuth;

    public Identity(String userId, UserType userType) {
        this.userId = userId;
        this.userType = userType;

    }

    public Identity(String userId, UserType userType, boolean isSelfAuth) {
        this.userId = userId;
        this.userType = userType;
        this.isSelfAuth = isSelfAuth;
    }

    public Identity(String sessionStateId) {
        this.sessionStateId = sessionStateId;
    }
}
