package com.reqlint.core.surefire.report;

import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;
import org.apache.commons.lang3.StringUtils;

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

        private enum Status {
            CONTEXT_BUILD,
            PREPARATION,
            OPERATION,
            EXPECTATION
        };

        private LocalDateTime timeStamp = LocalDateTime.now();
        private String name;
        private TestRunStage preparation;
        private final List<TestRunStage> stages = new ArrayList<>();

        private String ignoreTrailing = "";

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

        public Builder ignoreTrailing(String ignoreTrailing) {
            this.ignoreTrailing = ignoreTrailing;
            return this;
        }

        public Builder output(String output) {
            return output(output.split("(\\r\\n|\\r|\\n)"));
        }

        /**
         * @param outputs The raw output logs captured during the test.
         * @return This builder.
         */
        public Builder output(String ... outputs) {
            TestRunStageStep.Builder stepBuilder = null;
            var stageBuilder = TestRunStage.builder();
            Status status = Status.CONTEXT_BUILD;

            for(String line: outputs) {
                // Ignore trailing and ignoring sequences:
                line = trim(line);

                // Check for delimiting patterns in the line:
                MatchingLiteral<LogPatternsAndFormats> matchingLiteral = FindMatchingLiteral.findClosestMatch(LogPatternsAndFormats.class, line);

                // If none:
                if (matchingLiteral == null) {
                    // Add the line to the current step:
                    if (stepBuilder!=null) {
                        stepBuilder.appendToOutput(line);
                    }

                    // Fetch the next line:
                    continue;
                }

                // Depending on the delimiting pattern found:
                switch (matchingLiteral.literal()) {
                    case PREPARE -> {
                        stageBuilder = TestRunStage.builder();
                        stageBuilder.ordinal(0);
                        stageBuilder.title("Preparation");

                        stepBuilder = null;

                        status = Status.PREPARATION;
                    }

                    case STAGE -> {
                        stageBuilder.addOperation(stepBuilder);
                        if (status == Status.PREPARATION || status == Status.CONTEXT_BUILD) {
                            this.preparation = stageBuilder.build();
                        } else {
                            if (status == Status.OPERATION) {
                                stageBuilder.addOperation(stepBuilder);
                            } else {
                                stageBuilder.addExpectation(stepBuilder);
                            }
                            this.stages.add(stageBuilder.build());
                        }
                        stageBuilder = TestRunStage.builder();
                        stageBuilder.ordinal(Integer.parseInt(matchingLiteral.group(3)));
                        stageBuilder.title(matchingLiteral.group(4));

                        stepBuilder = null;

                        status = Status.OPERATION;
                    }

                    case GIVEN, WHEN, AND, STAR -> {
                        if (status == Status.EXPECTATION) {
                            stageBuilder.addExpectation(stepBuilder);
                        } else {
                            stageBuilder.addOperation(stepBuilder);
                        }

                        stepBuilder = TestRunStageStep.builder();
                        stepBuilder.ordinal(Integer.parseInt(matchingLiteral.group(1)));
                        stepBuilder.title(matchingLiteral.group(3));
                    }

                    case THEN -> {
                        if (status == Status.EXPECTATION) {
                            stageBuilder.addExpectation(stepBuilder);
                        } else {
                            stageBuilder.addOperation(stepBuilder);
                        }

                        status = Status.EXPECTATION;

                        stepBuilder = TestRunStageStep.builder();
                        stepBuilder.ordinal(Integer.parseInt(matchingLiteral.group(1)));
                        stepBuilder.title(matchingLiteral.group(3));
                    }
                }
            }
            if (status == Status.EXPECTATION) {
                stageBuilder.addExpectation(stepBuilder);
            } else {
                stageBuilder.addOperation(stepBuilder);
            }
            if (status == Status.PREPARATION) {
                this.preparation = stageBuilder.build();
            } else {
                this.stages.add(stageBuilder.build());
            }

            return this;
        }

        /**
         * @param failure The raw error logs captured during the test.
         * @return This builder.
         */
        public Builder failure(String failure) {
            return this;
        }

        private String trim(String line) {
            if (!StringUtils.isBlank(ignoreTrailing)) {
                return StringUtils.stripEnd(line, ignoreTrailing);
            }
            return line;
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

    /**
     * @return {@code true} if either the preparation or any stage is failed.
     */
    public boolean isFailed() {
        if (preparation().isFailed()) {
            return true;
        }
        for (TestRunStage stage : stages) {
            if (stage.isFailed()) {
                return true;
            }
        }
        return false;
    }
}
