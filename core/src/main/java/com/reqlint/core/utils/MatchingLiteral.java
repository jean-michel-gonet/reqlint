package com.reqlint.core.utils;

import java.util.regex.Matcher;

/**
 * The
 * @param literal
 * @param matcher
 * @param <T>
 */
public record MatchingLiteral<T extends Enum<T>>(T literal, Matcher matcher) {
    /**
     * Proxy method to {@link Matcher#group(int)}
     * @param group The index of the capturing group.
     * @return The (possibly empty) content of the specified group.
     */
    public String group(int group) {
        return matcher.group(group);
    }

    /**
     * Proxy method to {@link Matcher#start()}
     * @return The index of the first character matched.
     */
    public int start() {
        return matcher.start();
    }

    /**
     * Proxy method to {@link Matcher#end()}
     * @return The index of the last character matched.
     */
    public int end() {
        return matcher.end();
    }
}
