package com.reqlint.core.input.surefire;

import com.reqlint.core.output.surefire.MarkDownFormatter;
import com.reqlint.core.output.surefire.StepDescription;
import com.reqlint.core.utils.AssociatedPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * List of formats to use in logs, and patterns to recognize them.
 */
public enum LogPatternsAndFormats implements AssociatedPattern {
    PREPARE("#\\s+Preparation of ([^-]*)\\s+-\\s+(.*)$",
            "# Preparation of %s - %s"),
    STAGE("#+\\s+Step\\s+([0-9]+)\\s+-\\s+(Given|When|Then|\\*)\\s+Stage\\s+([0-9]+)\\s+-\\s+(.*)$",
            "# Step %d - %s - Stage %d - %s"),
    GIVEN("#\\s+Step\\s+([0-9]+)\\s+-\\s+(Given)\\s+(.*)$",
            "# Step %d - Given %s"),
    AND("#\\s+Step\\s+([0-9]+)\\s+-\\s+(And)\\s+(.*)$",
            "# Step %d - And %s"),
    WHEN("#\\s+Step\\s+([0-9]+)\\s+-\\s+(When)\\s+(.*)$",
            "# Step %d - When %s"),
    THEN("#\\s+Step\\s+([0-9]+)\\s+-\\s+(Then)\\s+(.*)$",
            "# Step %d - Then %s"),
    STAR("#\\s+Step\\s+([0-9]+)\\s+-\\s+(\\*)\\s+(.*)$",
            "# Step %d - * %s"),
    DATATABLE_SEPARATOR("^\\|(-+\\|)+", ""),
    DATATABLE("^\\|([^|]+\\|)+", "");

    private final Pattern pattern;
    private final String format;

    LogPatternsAndFormats(String sPattern, String sFormat) {
        this.pattern = Pattern.compile(sPattern);
        this.format = sFormat;
    }

    public static String preparationOf(String feature, String scenario) {
        return String.format(PREPARE.format, feature, scenario);
    }

    public static String stepOf(StepDescription description) {
        int stepNumber = description.number();
        String stepDescription = description.text();
        String line = switch (description.keyword()) {
            case "Given" -> String.format(GIVEN.format, stepNumber, stepDescription);
            case "When" -> String.format(WHEN.format, stepNumber, stepDescription);
            case "Then" -> String.format(THEN.format, stepNumber, stepDescription);
            case "And" -> String.format(AND.format, stepNumber, stepDescription);
            case "*" -> String.format(STAR.format, stepNumber, stepDescription);
            default -> throw new IllegalArgumentException("Invalid keyword: " + description.keyword());
        };
        if (!description.cells().isEmpty()) {
            return line + "\r\n" + new MarkDownFormatter<>(description.cells());
        }
        return line;
    }

    @Override
    public Matcher match(String line) {
        return pattern.matcher(line);
    }
}
