package com.otg.tech.util.commons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class Utils {
    private static final String OPTIONAL_DATE = "2023-01-01";
    private static final String SLASH = "/";
    private static final String HYPHEN = "-";
    private static final char[] ALPHA_NUMERIC =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String EX_MSG = "Exception in converting object to string";
    private static final char[] NUMERIC = "0123456789".toCharArray();

    public static String maskToNull(String val) {
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        return val.trim();
    }

    public static String maskToEmpty(String val) {
        if (StringUtils.isEmpty(val)) {
            return "";
        }
        return val.trim();
    }

    public static String randomString(int length) {
        SecureRandom r = new SecureRandom();
        StringBuilder random = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (ALPHA_NUMERIC.length * r.nextDouble());
            random.append(ALPHA_NUMERIC[index]);
        }
        return random.toString();
    }

    public static String getRandomString(int length, boolean useLetters, boolean useNumbers) {
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static String currentTimeStamp(String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    public static String formatStringDate(
            String currentPattern,
            String pattern,
            String date) {
        if (StringUtils.isEmpty(currentPattern)
                || StringUtils.isEmpty(date)
                || StringUtils.isEmpty(pattern)
        )
            return null;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern(currentPattern);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate
                .parse(date, parser)
                .format(formatter);
    }

    public static String formatForAny(
            final String strDate,
            final String format,
            final String... parseFormats) {
        for (String s : parseFormats) {
            if (isSafeFormat(strDate, s)) {
                Date date = parse(s, strDate);
                if (date != null) {
                    return new SimpleDateFormat(format).format(date);
                }
            }
        }
        return OPTIONAL_DATE;
    }

    private static boolean isSafeFormat(String date, String format) {
        try {
            if (dateCheck(date, SLASH, format)) return false;
            return !dateCheck(date, HYPHEN, format);
        } catch (Exception e) {
            log.error("Date is not in the pattern : {}", format, e);
            return false;
        }
    }

    private static boolean dateCheck(String date, String symbol, String format) {
        if (date.contains(symbol) && format.contains(symbol)) {
            String[] formatArray = format.split(symbol);
            String[] dateArray = date.split(symbol);
            if (formatArray.length != dateArray.length)
                return true;
            for (int i = 0; i < formatArray.length; i++) {
                if (formatArray[i].length() != dateArray[i].length()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Date parse(final String format, final String date) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Exception e) {
            log.error("Error in parsing date : {} in format : {}",
                    date,
                    format
            );
            return null;
        }
    }

    public static <T> String toJsonString(T t) {
        try {
            OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            return OBJECT_MAPPER.writeValueAsString(t);
        } catch (Exception e) {
            log.error(EX_MSG, e);
            return null;
        }
    }

    public static <T> T toJavaPojo(String strJson, Class<T> cls) {
        try {
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            return OBJECT_MAPPER.readValue(strJson, cls);
        } catch (Exception e) {
            log.error(EX_MSG, e);
            return null;
        }
    }

    public static <T> T toJavaListPojo(String strJson, TypeReference<T> typeReference) {
        try {
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            return OBJECT_MAPPER.readValue(strJson, typeReference);
        } catch (Exception e) {
            log.error(EX_MSG, e);
            return null;
        }
    }

    public static <T> T deserialize(Object json, Class<T> cls) {
        final String strData = toJsonString(json);
        try {
            return OBJECT_MAPPER.readValue(strData, cls);
        } catch (Exception e) {
            log.error("Exception occurred while deserializing type value.", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> jsonPojoToMap(T data) {
        final String strData = Utils.toJsonString(data);
        if (strData != null) {
            return Utils.toJavaPojo(strData, Map.class);
        }
        return Collections.emptyMap();
    }

    public static String getRRN() {
        Date today = new Date();
        LocalDateTime currentDate = LocalDateTime.now();
        int year = currentDate.getYear();
        int hour = currentDate.getHour();
        char y = lastChar(year);
        String mmm = new SimpleDateFormat("D").format(today);
        String ssssssss = hour + randomNumber(6);
        return y + mmm + ssssssss;
    }

    public static String randomNumber(int length) {
        SecureRandom r = new SecureRandom();
        StringBuilder random = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (NUMERIC.length * r.nextDouble());
            random.append(NUMERIC[index]);
        }
        return random.toString();
    }

    public static char lastChar(int value) {
        String stringValue = String.valueOf(value);
        return stringValue.charAt(stringValue.length() - 1);
    }

    public static OffsetDateTime getOffSetDateTime() {
        return OffsetDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}
