package com.otg.tech.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@AllArgsConstructor
@Getter
public enum ErrorExceptionCodes {

    AES0401("AES0401", "User Token has expired or not authorized.", UNAUTHORIZED.value()),
    AES0500("AES0500", "AES Encryption Failed.", INTERNAL_SERVER_ERROR.value());

    private final String code;
    private final String message;
    private final int httpStatus;
}
