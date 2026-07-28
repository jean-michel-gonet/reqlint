package com.reqlint.core.latex.reader;

import com.reqlint.core.utils.AssociatedPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ExpansionCommand implements AssociatedPattern {
    IMPORT(Pattern.compile("(\\\\)(import)\\{([^}]+)}\\{([^}]+)}")),
    SUBIMPORT(Pattern.compile("(\\\\)(subimport)\\{([^}]+)}\\{([^}]+)}")),
    INPUT(Pattern.compile("(\\\\)(input)\\{([^}]+)}")),
    INCLUDE(Pattern.compile("(\\\\)(include)\\{([^}]+)}")),
    COMMENT(Pattern.compile("(^|[^\\\\])(%)(.*)"));

    private final Pattern pattern;

    ExpansionCommand(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Matcher find(String line) {
        return pattern.matcher(line);
    }
}
