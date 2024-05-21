package com.otg.tech.notification.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
@Getter
public enum ErrorExceptionCodes {

    NOTIFY0400("NOTIFY0400", "Invalid Request", BAD_REQUEST.value()),
    NOTIFY0500("NOTIFY0500", "Internal server error", INTERNAL_SERVER_ERROR.value()),
    NOTIFY1001("NOTIFY1001", "Channel does not exist", NOT_FOUND.value()),
    NOTIFY1002("NOTIFY1002", "Provider does not exist", NOT_FOUND.value()),
    NOTIFY1003("NOTIFY1003", "Template code is already exists", BAD_REQUEST.value()),
    NOTIFY1004("NOTIFY1004", "Template id does not exist", NOT_FOUND.value()),

    NOTIFY2001("NOTIFY2001", "Mqtt notification push failed", INTERNAL_SERVER_ERROR.value()),
    NOTIFY2002("NOTIFY2002", "Unable to connect with AWS IOT Core", INTERNAL_SERVER_ERROR.value());

    private final String code;
    private final String message;
    private final int httpStatus;
}
