package com.reqlint.sandbox.cucumber.renderers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * Utility class to render values as strings.
 */
public class ValueRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueRenderer.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.000");

    public static String renderInteger(Supplier<Integer> supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public static String renderString(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public static String renderBigDecimal(Supplier<BigDecimal> supplier) {
        try {
            return DECIMAL_FORMAT.format(supplier.get());
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public static String renderLocalTime(Supplier<LocalDateTime> supplier) {
        try {
            LocalTime localTime = supplier.get().toLocalTime();
            return localTime.format(DATE_TIME_FORMATTER);
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    private ValueRenderer() {
        // This is a utility class
    }

}
