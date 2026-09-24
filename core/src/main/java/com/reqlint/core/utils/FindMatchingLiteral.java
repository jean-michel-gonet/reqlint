package com.reqlint.core.utils;

import java.util.regex.Matcher;

/**
 * Utility class with tools related to pattern matching.
 */
public class FindMatchingLiteral {

    /**
     * Looks for the match that is closest to the beginning of the line.
     * @param clazz The enumeration
     * @param line The line to look for matches.
     * @return The literal and an initialized matcher. {@code null} if there are no matches.
     * @param <T> An enumerator that also implements {@link AssociatedPattern}.
     */
    public static <T extends Enum<T> & AssociatedPattern> MatchingLiteral<T> findClosestMatch(Class<T> clazz, String line) {
        int closestStart = line.length();
        MatchingLiteral<T> matchingLiteral = null;
        for (T enumConstant : clazz.getEnumConstants()) {
            Matcher matcher = enumConstant.match(line);
            if (matcher.find() && matcher.start() < closestStart) {
                closestStart = matcher.start();
                matchingLiteral = new MatchingLiteral<>(enumConstant, matcher);
            }
        }
        return matchingLiteral;
    }

    /**
     * Utility class.
     * Call the static methods directly.
     */
    private FindMatchingLiteral() {
        // Nothing to do.
    }
}
