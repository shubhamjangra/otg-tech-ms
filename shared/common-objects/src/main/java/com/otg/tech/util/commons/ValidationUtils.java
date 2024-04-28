package com.otg.tech.util.commons;

public interface ValidationUtils {

    static boolean isStringEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }
}
