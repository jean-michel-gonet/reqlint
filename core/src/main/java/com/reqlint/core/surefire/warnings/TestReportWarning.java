package com.reqlint.core.surefire.warnings;

import com.reqlint.core.specification.items.TestCase;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * A warning found while rendering the test report.
 */
public class TestReportWarning implements Comparable<TestReportWarning> {
    private static final Comparator<TestReportWarning> COMPARATOR =
            Comparator.comparing(TestReportWarning::testCase);

    private final TestCase testCase;
    private final String description;

    /**
     * Protected class constructor.
     * Use one of its descendants.
     * @param testCase The test case concerned by the warning.
     * @param description The description of the warning.
     */
    protected TestReportWarning(TestCase testCase, String description) {
        this.testCase = testCase;
        this.description = description;
    }

    /**
     * @return The test case concerned by the warning.
     */
    public TestCase testCase() {
        return testCase;
    }

    /**
     * @return The description of the warning.
     */
    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TestReportWarning other) {
            return Objects.equals(testCase, other.testCase) && Objects.equals(description, other.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return testCase.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return testCase.identifier() + ": " + description;
    }

    @Override
    public int compareTo(@NonNull TestReportWarning o) {
        return COMPARATOR.compare(this, o);
    }
}
