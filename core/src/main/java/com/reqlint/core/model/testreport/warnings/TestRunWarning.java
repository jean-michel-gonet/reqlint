package com.reqlint.core.model.testreport.warnings;

import com.reqlint.core.model.specification.items.TestCase;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * A warning found while rendering the test report.
 */
public class TestRunWarning implements Comparable<TestRunWarning> {
    private static final Comparator<TestRunWarning> COMPARATOR =
            Comparator.comparing(TestRunWarning::testCase);

    private final TestCase testCase;
    private final String description;

    /**
     * Protected class constructor.
     * Use one of its descendants.
     * @param testCase The test case concerned by the warning.
     * @param description The description of the warning.
     */
    protected TestRunWarning(TestCase testCase, String description) {
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
        if (o instanceof TestRunWarning other) {
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
    public int compareTo(@NonNull TestRunWarning o) {
        return COMPARATOR.compare(this, o);
    }
}
