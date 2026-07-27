package com.reqlint.core.utils;

import java.util.regex.Matcher;

public interface AssociatedPattern {
    Matcher find(String line);
}
