package com.otg.tech.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException {

    private int code;
    private String errorCode;
    private String errorMessage;
    private String errorStatus;
    private String requestId;
    private OffsetDateTime tillBlockTime;
    private BigDecimal availableLimit;
    private BigDecimal consumedAmount;

    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(int code, String errorCode, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(Throwable cause, String errorCode, String errorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(String errorCode, String errorMessage, String errorStatus) {
        super(errorMessage);
        this.errorStatus = errorStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(int code, String errorCode, String errorMessage, BigDecimal availableLimit,
                             BigDecimal consumedAmount) {
        super(errorMessage);
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.availableLimit = availableLimit;
        this.consumedAmount = consumedAmount;
    }
}
