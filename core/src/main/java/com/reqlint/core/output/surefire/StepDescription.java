package com.reqlint.core.output.surefire;

import java.util.Collections;
import java.util.List;

public record StepDescription(
        int number,
        String keyword,
        String text,
        List<List<String>> cells) {
    public StepDescription(int number, String keyword, String text) {
        this(number, keyword, text, Collections.emptyList());
    }
}
