package com.otg.tech.auth.claims;

@SuppressWarnings("unused")
public enum UserType {
    MERCHANT,
    EMPLOYEE;

    public static UserType fromString(String type) {
        for (UserType userType : UserType.values()) {
            if (userType.name().equalsIgnoreCase(type)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + type);
    }
}
