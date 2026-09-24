package com.reqlint.core.model.testreport.items;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One test that has run.
 * @param timeStamp The timestamp, when the test was ran.
 * @param title The title of the test.
 * @param preparation The preparation stage.
 * @param stages The list of stages in the test.
 */
public record TestRun(LocalDateTime timeStamp, String title, TestRunStage preparation, List<TestRunStage> stages) {

    /**
     * @return A convenient builder to create new instances of {@link TestRun}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A convenient builder to create new instances of {@link TestRun}.
     */
    public static class Builder {
        private LocalDateTime timeStamp = LocalDateTime.now();
        private String name;
        private TestRunStage preparation;
        private final List<TestRunStage> stages = new ArrayList<>();

        /**
         * @return A new instance.
         */
        public TestRun build() {
            return new TestRun(this);
        }

        /**
         * @param timeStamp When the test report was made.
         * @return This builder.
         */
        public Builder timeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        /**
         * @param name The name of the test report.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the preparation for the test run.
         * @param preparationBuilder The preparation builder.
         * @return This builder.
         */
        public Builder preparation(TestRunStage.Builder preparationBuilder) {
            if (preparationBuilder != null) {
                return preparation(preparationBuilder.build());
            }
            return this;
        }

        /**
         * Sets the preparation for the test run.
         * @param preparation The preparation
         * @return This buider.
         */
        public Builder preparation(TestRunStage preparation) {
            this.preparation = preparation;
            return this;
        }

        /**
         * Adds the stage to the test run.
         * @param stageBuilder The stage builder.
         * @return This builder.
         */
        public Builder addStage(TestRunStage.Builder stageBuilder) {
            if (stageBuilder != null) {
                return addStage(stageBuilder.build());
            }
            return this;
        }

        /**
         * Adds the stage to the test run.
         * @param stage The stage.
         * @return This builder.
         */
        public Builder addStage(TestRunStage stage) {
            this.stages.add(stage);
            return this;
        }
    }

    /**
     * Builder constructor.
     * @see #builder()
     * @param builder The builder.
     */
    private TestRun(Builder builder) {
        this(builder.timeStamp, builder.name, builder.preparation, builder.stages);
    }
}
