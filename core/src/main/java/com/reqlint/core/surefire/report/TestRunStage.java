package com.reqlint.core.surefire.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes one stage in the test procedure.
 * @param ordinal The stage number.
 * @param title The title of the stage.
 * @param operations The list of operations.
 * @param expectations The list of expectations.
 */
public record TestRunStage(int ordinal, String title, List<TestRunStageStep> operations, List<TestRunStageStep> expectations) {

    /**
     * @return Builder to help creating new instances of {@link TestRunStage}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to help creating new instances of {@link TestRunStage}.
     */
    public static class Builder {
        private int ordinal;
        private String title;
        private final List<TestRunStageStep> operations = new ArrayList<>();
        private final List<TestRunStageStep> expectations = new ArrayList<>();

        /**
         * @return A new instance.
         */
        public TestRunStage build() {
            return new TestRunStage(this);
        }

        /**
         * @param ordinal The stage number.
         * @return The builder
         */
        public Builder ordinal(int ordinal) {
            this.ordinal = ordinal;
            return this;
        }

        /**
         * @param title The stage title.
         * @return The builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Adds one operation to the stage.
         * @param operation The operation.
         * @return The builder.
         */
        public Builder addOperation(TestRunStageStep.Builder operation) {
            if (operation != null) {
                return addOperation(operation.build());
            }
            return this;
        }

        /**
         * Adds one operation to the stage.
         * @param operation The operation.
         * @return The builder.
         */
        public Builder addOperation(TestRunStageStep operation) {
            this.operations.add(operation);
            return this;
        }

        /**
         * Adds one expectation to the stage.
         * @param expectation The expectation.
         * @return The builder.
         */
        public Builder addExpectation(TestRunStageStep.Builder expectation) {
            if (expectation != null) {
                return addExpectation(expectation.build());
            }
            return this;
        }

        /**
         * Adds one expectation to the stage.
         * @param expectation The expectation.
         * @return The builder.
         */
        public Builder addExpectation(TestRunStageStep expectation) {
            this.expectations.add(expectation);
            return this;
        }
    }

    /**
     * Private constructor.
     * @see #builder()
     * @param builder The builder.
     */
    private TestRunStage(Builder builder) {
        this(builder.ordinal,
                builder.title,
                Collections.unmodifiableList(builder.operations),
                Collections.unmodifiableList(builder.expectations));
    }

    /**
     * @return {@code true} If any operation or expectation is failed.
     */
    public boolean isFailed() {
        for (TestRunStageStep operation : operations) {
            if (operation.isFailed()) {
                return true;
            }
        }
        for (TestRunStageStep expectation : expectations) {
            if (expectation.isFailed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ordinal + " - " + title;
    }
}
