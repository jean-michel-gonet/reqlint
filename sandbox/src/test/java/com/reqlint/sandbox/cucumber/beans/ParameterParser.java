package com.reqlint.sandbox.cucumber.beans;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convenience class to read parameters given in cucumber scenarios.
 */
public class ParameterParser {
    private static final Pattern LOCAL_TIME_PATTERN = Pattern
            .compile("([0-9]{2}):?([0-9]{2})?:?([0-9]{2})?\\.?([0-9]+)?");

    /**
     * Parses an optional integer.
     * @param name The name of the parameter, to raise an explicit exception.
     * @param value The value of the parameter.
     * @return Either the value or {@code null}.
     */
    public static Integer parseOptionalInteger(Object name, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse " + name + " as an integer: " + value + " - " + e.getMessage());
        }
    }

    /**
     * Alias to {@link #parseOptionalInteger}
     * @param name The name of the field in the entry.
     * @param entry The entry.
     * @return The value.
     */
    public static Integer parseOptionalInteger(Object name, Map<String, String> entry) {
        String sName = name.toString();
        return parseOptionalInteger(name, entry.get(sName));
    }

    /**
     * Parses a mandatory integer.
     * @param name The name of the parameter, to raise an explicit exception.
     * @param value The value of the parameter.
     * @return The value.
     */
    public static int parseMandatoryInteger(Object name, String value) {
        Integer i = parseOptionalInteger(name, value);
        if (i == null) {
            throw new IllegalArgumentException("Missing " + name + " field, which is a mandatory integer");
        }
        return i;
    }

    /**
     * Alias to {@link #parseMandatoryInteger}
     * @param name The name of the field in the entry.
     * @param entry The entry.
     * @return The value.
     */
    public static int parseMandatoryInteger(Object name, Map<String, String> entry) {
        String sName = name.toString();
        return parseMandatoryInteger(name, entry.get(sName));
    }

    /**
     * Parses a mandatory string.
     * It returns the provided string.
     * It raises an exception if the string is empty.
     * @param name The name of the parameter, to raise an explicit exception.
     * @param value The value of the string.
     * @return The value.
     */
    public static String parseMandatoryString(Object name, String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Missing " + name + " field, which is a mandatory string");
        }
        return value;
    }

    /**
     * Alias to {@link #parseMandatoryString}
     * @param name The name of the field in the entry.
     * @param entry The entry.
     * @return The value.
     */
    public static String parseMandatoryString(Object name, Map<String, String> entry) {
        String sName = name.toString();
        return parseMandatoryString(name, entry.get(sName));
    }

    /**
     * Parses an optional local time.
     * @param name The name of the parameter, to raise an explicit exception.
     * @param value The value of the parameter.
     * @return The value.
     */
    public static LocalTime parseOptionalDateTime(Object name, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Matcher matcher = LOCAL_TIME_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(value + " is not a correctly formatted local time. It should be as '13:25:34.456'");
        }

        String sHour = matcher.group(1);
        int hour = Integer.parseInt(sHour);

        String sMinute = matcher.group(2);
        int minute = sMinute == null ? 0 : Integer.parseInt(sMinute);

        String sSecond = matcher.group(3);
        int second = sSecond == null ? 0 : Integer.parseInt(sSecond);

        String sDecimals = matcher.group(4);
        int nanos;
        if (sDecimals == null) {
            nanos = 0;
        } else {
            int l = sDecimals.length();
            BigDecimal decimals = new BigDecimal(sDecimals)
                    .multiply(BigDecimal.TEN.pow(9))
                    .divide(BigDecimal.TEN.pow(l), RoundingMode.FLOOR);
            nanos = decimals.intValueExact();
        }

        return LocalTime.of(hour, minute, second, nanos);
    }

    /**
     * Parses a mandatory local time.
     * @param name The name of the parameter, to raise an explicit exception.
     * @param value The value of the parameter.
     * @return The value.
     */
    public static LocalTime parseMandatoryLocalTime(Object name, String value) {
        LocalTime localTime = parseOptionalDateTime(name, value);
        if (localTime == null) {
            throw new IllegalArgumentException("Missing " + name + " field, which is a mandatory string");
        }
        return localTime;
    }

    /**
     * Alias to {@link #parseMandatoryLocalTime}
     * @param name The name of the field in the entry.
     * @param entry The entry.
     * @return The value.
     */
    public static LocalTime parseMandatoryLocalTime(Object name, Map<String, String> entry) {
        String sName = name.toString();
        return parseMandatoryLocalTime(sName, entry.get(sName));
    }

    private ParameterParser() {
        // This is a utility class
    }


}
