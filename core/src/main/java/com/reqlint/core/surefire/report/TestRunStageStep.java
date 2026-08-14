package com.reqlint.core.surefire.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes one step in a test stage.
 * @param ordinal The step number.
 * @param title Textual description.
 * @param output The logs entries associated to this step.
 */
public record TestRunStageStep(int ordinal, String title, List<String> output) {

    /**
     * @return A builder, to create new instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder, to create new instances.
     */
    public static class Builder {
        private int ordinal;
        private String title;
        private final List<String> output = new ArrayList<>();

        /**
         * @return A new instance.
         */
        public TestRunStageStep build() {
            return new TestRunStageStep(this);
        }

        /**
         * @param ordinal The step number.
         * @return This builder.
         */
        public Builder ordinal(int ordinal) {
            this.ordinal = ordinal;
            return this;
        }

        /**
         * @param title The step title.
         * @return This builder.
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Adds a line to the output of the step.
         * @param output A new line.
         * @return This builder.
         */
        public Builder appendToOutput(String output) {
            this.output.add(output);
            return this;
        }
    }

    /**
     * Private constructor.
     * @see #builder()
     * @param builder A builder.
     */
    private TestRunStageStep(Builder builder) {
        this(builder.ordinal, builder.title, builder.output);
    }

    @Override
    public String toString() {
        return ordinal + " - " + title;
    }

}
