package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ValueOf {

    private ValueOf() {
        throw new IllegalStateException("ValueOf is utility class");
    }

    /**
     * if String, validation true for 'empty string' and 'null' text
     */
    private static boolean isNull(Object value) {
        if (Objects.isNull(value)) return true;
        if (value instanceof String) {
            String valueStr = (String) value;
            return valueStr.trim().isEmpty();
        }
        return false;
    }

    /**
     * has null value
     */
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

    public static Float init(Float value, Float defaultValue) {
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

    public static Boolean init(Boolean value, Boolean defaultValue) {
        if (isNull(value)) return defaultValue;
        return value;
    }

    public static boolean nonNull(Object value) {
        return !isNull(value);
    }

    public static boolean nonNull(String value) {
        return !isNull(value);
    }

    public static boolean nonNull(Double value) {
        return !isNull(value);
    }

    public static boolean nonNull(Boolean value) {
        return !isNull(value);
    }

    public static boolean equalText(String string, String... equals) {
        if (ValueOf.isNull(string)) return false;
        for (String s : equals) {
            if (ValueOf.isNull(s)) continue;
            if (string.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public static boolean notEqualText(String string, String... equals) {
        if (ValueOf.isNull(string)) return true;
        for (String s : equals) {
            if (ValueOf.isNull(s)) continue;
            if (string.equalsIgnoreCase(s)) return false;
        }
        return true;
    }

    public static boolean containsText(String string, String... equals) {
        if (ValueOf.isNull(string)) return false;
        for (String s : equals) {
            if (ValueOf.isNull(s)) continue;
            if (string.contains(s)) return true;
        }
        return false;
    }

    public static boolean containsTextIgnoreCase(String string, String... equals) {
        if (ValueOf.isNull(string)) return false;
        for (String s : equals) {
            if (ValueOf.isNull(s)) continue;
            if (string.toUpperCase().contains(s.toUpperCase())) return true;
        }
        return false;
    }

    public static boolean notContainsTextIgnoreCase(String string, String... equals) {
        if (ValueOf.isNull(string)) return true;
        for (String s : equals) {
            if (ValueOf.isNull(s)) continue;
            if (string.toUpperCase().contains(s.toUpperCase())) return false;
        }
        return true;
    }

    public static boolean startWithText(String string, String... prefix) {
        if (ValueOf.isNull(string)) return false;
        for (String s : prefix) {
            if (ValueOf.isNull(s)) continue;
            if (string.startsWith(s)) return true;
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

    public static boolean minMaxLength(int min, int max, String value) {
        if (Objects.isNull(value)) return false;
        int length = value.trim().length();
        return minMaxLength(min, max, length);
    }

    public static boolean minMaxLength(int min, int max, int length) {
        return length >= min && length <= max;
    }

    public static boolean notLength(String value, int length) {
        return !lengthEquals(value, length);
    }

    public static boolean lengthEquals(String value, int length) {
        if (Objects.isNull(value)) return true;
        return (value.length() == length);
    }

    public static boolean isNumber(String value) {
        if (Objects.isNull(value)) return false;
        boolean result = isValidRegex("^\\d+$", value);
        log.debug("is number {}, {}", value, result);
        return result;
    }

    public static boolean isNotNumber(String value) {
        return !isNumber(value);
    }

    public static boolean isValidRegex(String regex, String value) {
        if (Objects.isNull(value)) return false;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        return m.find();
    }

    public static Optional<String> ofNullable(String value) {
        return Optional.ofNullable(init(value, null));
    }


    public static Optional<Double> ofNullable(Double value) {
        return Optional.ofNullable(init(value, null));
    }

    public static Optional<Long> ofNullable(Long value) {
        return Optional.ofNullable(init(value, null));
    }

    public static Optional<Float> ofNullable(Float value) {
        return Optional.ofNullable(init(value, null));
    }

    public static Optional<Integer> ofNullable(Integer value) {
        return Optional.ofNullable(init(value, null));
    }

    public static Optional<Boolean> ofNullable(Boolean value) {
        return Optional.ofNullable(init(value, null));
    }


    public static boolean isDouble(String income) {
        try {
            Double.valueOf(income);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
