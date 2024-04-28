package com.otg.tech.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ApplicationException extends RuntimeException {

    private int code;
    private String errorCode;
    private String errorMessage;
    private String errorStatus;

    public ApplicationException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApplicationException(int code, String errorCode, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApplicationException(Throwable cause, String errorCode, String errorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApplicationException(String errorCode, String errorMessage, String errorStatus) {
        super(errorMessage);
        this.errorStatus = errorStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ApplicationException rethrow(ApplicationException exception) {
        return new ApplicationException(
                exception.errorCode,
                exception.errorMessage,
                exception.errorStatus
        );
    }
}
