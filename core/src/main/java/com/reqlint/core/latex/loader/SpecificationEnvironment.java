package com.reqlint.core.latex.loader;

import com.reqlint.core.utils.AssociatedPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SpecificationEnvironment implements AssociatedPattern {
    EQUIPMENT_REQUIREMENT(Pattern.compile("\\\\begin\\{equipmentrequirement}")),
    SOFTWARE_REQUIREMENT(Pattern.compile("\\\\begin\\{softwarerequirement}")),
    TEST_CASE(Pattern.compile("\\\\begin\\{testcase}"));

    private final Pattern pattern;

    SpecificationEnvironment(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Matcher find(String line) {
        return pattern.matcher(line);
    }
}
