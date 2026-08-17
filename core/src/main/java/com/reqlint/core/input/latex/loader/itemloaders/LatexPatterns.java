package com.reqlint.core.input.latex.loader.itemloaders;

import java.util.regex.Pattern;

/**
 * A collection of LaTeX patterns to use while loading the specification tree.
 */
class LatexPatterns {
    /**
     * Pattern to match two arguments.
     * In the example below, it catches {@code arg1} in group 1 and {@code arg1} in group 2:
     * <pre>
     *     \begin{environmentname}{arg1}{arg2}
     * </pre>
     */
    public static final Pattern TWO_ARGUMENTS = Pattern.compile("\\{([^}]+)}\\{([^}]+)}");

    /**
     * Pattern to match status with one argument.
     * In the example below, it catches {@code arg1} in group 1:
     * <pre>
     *     \status{arg1}
     * </pre>
     */
    public static final Pattern STATUS =  Pattern.compile("\\\\status\\{([^}]+)}");

    /**
     * Pattern to match derived with one argument.
     * In the example below, it catches {@code arg1} in group 1:
     * <pre>
     *     \derived{arg1}
     * </pre>
     */
    public static final Pattern DERIVED =  Pattern.compile("\\\\derived\\{([^}]+)}");

    /**
     * Pattern to match security with one argument.
     * It matches the example below:
     * <pre>
     *     \security
     * </pre>
     */
    public static final Pattern SECURITY =  Pattern.compile("\\\\security");

    /**
     * Pattern to match safety with one argument.
     * It matches the example below:
     * <pre>
     *     \safety
     * </pre>
     */
    public static final Pattern SAFETY =  Pattern.compile("\\\\safety");

    /**
     * Pattern to match {@code childof} with one argument.
     * In the example below, it catches {@code arg1} in group 1:
     * <pre>
     *     \childof{arg1}
     * </pre>
     */
    public static final Pattern CHILD_OF =  Pattern.compile("\\\\childof\\{([^}]+)}");

    private LatexPatterns() {
    }
}
