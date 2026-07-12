package com.reqlint.sandbox.cucumber.beans;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Convenience class to read parameters given in cucumber scenarios.
 */
public class ParameterParser {

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

    private ParameterParser() {
        // This is a utility class
    }


}
