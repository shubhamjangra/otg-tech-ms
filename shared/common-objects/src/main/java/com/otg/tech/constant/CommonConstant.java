package com.otg.tech.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstant {
    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String HYPHEN = "-";
    public static final String EMPTY = "";
    public static final String NO_AES_KEY = "No-Auth";
    public static final String AES_KEY = "Auth";
}
