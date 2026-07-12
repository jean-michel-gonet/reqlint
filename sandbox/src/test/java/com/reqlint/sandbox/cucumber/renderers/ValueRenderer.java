package com.reqlint.sandbox.cucumber.renderers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Utility class to render values as strings.
 */
public class ValueRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueRenderer.class);

    public static String parseInteger(Supplier<Integer> supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public static String parseString(Supplier<String> supplier) {
        try {
            return supplier.get();
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
