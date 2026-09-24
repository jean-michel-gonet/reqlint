package com.reqlint.core.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FindMatchingLiteralTest {
    private enum TextualNumbers implements AssociatedPattern {
        ONE(Pattern.compile("\\\\one")),
        TWO(Pattern.compile("\\\\two")),
        THREE(Pattern.compile("\\\\three"));

        private final Pattern pattern;

        TextualNumbers(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    @Test
    public void can_find_the_closest_match_one() {
        MatchingLiteral<TextualNumbers> matchingLiteral = FindMatchingLiteral
                .findClosestMatch(TextualNumbers.class, "\\one plus \\two equal \\three");
        Assertions.assertThat(matchingLiteral.literal()).isEqualTo(TextualNumbers.ONE);
        Assertions.assertThat(matchingLiteral.group(0)).isEqualTo("\\one");
        Assertions.assertThat(matchingLiteral.start()).isZero();
        Assertions.assertThat(matchingLiteral.end()).isEqualTo(4);
    }

    @Test
    public void can_find_the_closest_match_two() {
        MatchingLiteral<TextualNumbers> matchingLiteral = FindMatchingLiteral
                .findClosestMatch(TextualNumbers.class, "But \\two plus \\one also equal \\three");
        Assertions.assertThat(matchingLiteral.literal()).isEqualTo(TextualNumbers.TWO);
        Assertions.assertThat(matchingLiteral.group(0)).isEqualTo("\\two");
        Assertions.assertThat(matchingLiteral.start()).isEqualTo(4);
        Assertions.assertThat(matchingLiteral.end()).isEqualTo(8);
    }



}