package com.onevour.core.applications.commons;

import java.util.Objects;

public class ValueOf {

    /**
     * if String, validation true for 'empty string' and 'null' text
     */
    private static boolean isNull(Object value) {
        if (Objects.isNull(value)) return true;
        if (value instanceof String) {
            String valueStr = (String) value;
            if ("null".equalsIgnoreCase(valueStr)) return true;
            return valueStr.trim().isEmpty();
        }
        return false;
    }

    public static boolean isNull(Object... values) {
        for (Object o : values) {
            if (isNull(o)) return true;
        }
        return false;
    }

    public static boolean inverse(boolean value) {
        return !value;
    }

    public static String init(String value, String defaultValue) {
        if (isNull(value)) return defaultValue;
        return value;
    }

    public static Integer init(Integer value, Integer defaultValue) {
        if (isNull(value)) return defaultValue;
        return value;
    }

    public static Long init(Long value, Long defaultValue) {
        if (isNull(value)) return defaultValue;
        return value;
    }

    public static Double init(Double value, Double defaultValue) {
        if (isNull(value)) return defaultValue;
        return value;
    }

    public static boolean nonNull(String value) {
        return !isNull(value);
    }

    public static boolean equalText(String ip, String... values) {
        if (ValueOf.isNull(ip)) return false;
        for (String s : values) {
            if (ValueOf.isNull(s)) continue;
            if (ip.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public static boolean equalInt(Integer value, Integer... values) {
        if (ValueOf.isNull(value)) return false;
        for (Integer s : values) {
            if (ValueOf.isNull(s)) continue;
            if (value.intValue() == s.intValue()) return true;
        }
        return false;
    }
}
