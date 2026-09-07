package com.reqlint.core.input.surefire;

import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.model.testreport.items.TestRunStage;
import com.reqlint.core.model.testreport.items.TestRunStageStep;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Loads one test case from the surefire reports.
 * @see SurefireTestSuiteLoader
 */
public class SurefireTestCaseLoader {
    private static final Pattern DATATABLE_SEPARATOR = Pattern.compile("^\\|(-+\\|)+");
    private static final Pattern DATATABLE = Pattern.compile("^\\|([^|]+\\|)+");

    private enum Status {
        CONTEXT_BUILD,
        PREPARATION,
        OPERATION,
        EXPECTATION
    };

    private String ignoreTrailing;

    /**
     * Configures the test run loader to ignore this sequence at the end of the log entries.
     * @param ignoreTrailing The sequence to ignore
     * @return This loader, for convenience.
     */
    public SurefireTestCaseLoader ignoreTrailing(String ignoreTrailing) {
        this.ignoreTrailing = ignoreTrailing;
        return this;
    }

    /**
     * @param outputs The raw output logs captured during the test.
     * @return The loaded test run.
     */
    public TestRun.Builder output(String ... outputs) {
        TestRun.Builder testRunBuilder = TestRun.builder();

        TestRunStageStep.Builder stepBuilder = null;
        var stageBuilder = TestRunStage.builder();
        Status status = Status.CONTEXT_BUILD;

        for(String multiLines: outputs) {
            String[] lines = multiLines.split("[\\r\\n]+");
            for (String line : lines) {
                // Check for delimiting patterns in the line:
                MatchingLiteral<LogPatternsAndFormats> matchingLiteral = FindMatchingLiteral.findClosestMatch(LogPatternsAndFormats.class, line);

                // If none:
                if (matchingLiteral == null) {
                    // Add the line as output to the current step:
                    if (stepBuilder != null) {
                        stepBuilder.appendToOutput(line);
                    }

                    // Fetch the next line:
                    continue;
                }

                // If data table:
                if (matchingLiteral.literal() == LogPatternsAndFormats.DATATABLE) {
                    if (stepBuilder != null) {
                        if (stepBuilder.hasOutput()) {
                            stepBuilder.appendToOutput(line);
                        } else {
                            String[] cellRow = line.substring(1).split("\\s*\\|\\s*");
                            stepBuilder.appendArgumentRow(cellRow);
                        }
                    }
                    continue;
                }

                // Depending on the delimiting pattern found:
                switch (matchingLiteral.literal()) {
                    case PREPARE -> {
                        // Open next stage:
                        stageBuilder = TestRunStage.builder();
                        stageBuilder.ordinal(0);
                        stageBuilder.title("Preparation");

                        // Open next step:
                        stepBuilder = null;

                        // Sets the section:
                        status = Status.PREPARATION;
                    }

                    case STAGE -> {
                        // Close current step and stage:
                        switch (status) {
                            case PREPARATION -> {
                                stageBuilder.addOperation(stepBuilder);
                                testRunBuilder.preparation(stageBuilder.build());
                            }
                            case OPERATION -> {
                                stageBuilder.addOperation(stepBuilder);
                                testRunBuilder.addStage(stageBuilder);
                            }
                            case EXPECTATION -> {
                                stageBuilder.addExpectation(stepBuilder);
                                testRunBuilder.addStage(stageBuilder);
                            }
                        }

                        // Open next step
                        stepBuilder = null;

                        // Open next stage:
                        stageBuilder = TestRunStage.builder();
                        stageBuilder.ordinal(Integer.parseInt(matchingLiteral.group(3)));
                        stageBuilder.title(trim(matchingLiteral.group(4)));

                        // Sets the section:
                        status = Status.OPERATION;
                    }

                    case GIVEN, WHEN, AND, STAR -> {
                        switch (status) {
                            case PREPARATION, OPERATION -> stageBuilder.addOperation(stepBuilder);
                            case EXPECTATION -> stageBuilder.addExpectation(stepBuilder);
                        }

                        // Open next step:
                        stepBuilder = TestRunStageStep.builder();
                        stepBuilder.ordinal(Integer.parseInt(matchingLiteral.group(1)));
                        stepBuilder.title(trim(matchingLiteral.group(3)));
                    }

                    case THEN -> {
                        switch (status) {
                            case PREPARATION -> {
                                stageBuilder.addOperation(stepBuilder);
                            }
                            case OPERATION -> {
                                stageBuilder.addOperation(stepBuilder);
                                status = Status.EXPECTATION;
                            }
                            case EXPECTATION -> {
                                stageBuilder.addExpectation(stepBuilder);
                            }
                        }

                        stepBuilder = TestRunStageStep.builder();
                        stepBuilder.ordinal(Integer.parseInt(matchingLiteral.group(1)));
                        stepBuilder.title(trim(matchingLiteral.group(3)));
                    }
                }
            }
        }
        switch (status) {
            case PREPARATION -> {
                stageBuilder.addOperation(stepBuilder);
                testRunBuilder.preparation(stageBuilder.build());
            }
            case OPERATION -> {
                stageBuilder.addOperation(stepBuilder);
                testRunBuilder.addStage(stageBuilder);
            }
            case EXPECTATION -> {
                stageBuilder.addExpectation(stepBuilder);
                testRunBuilder.addStage(stageBuilder);
            }
        }

        return testRunBuilder;
    }

    private String trim(String line) {
        if (!StringUtils.isBlank(ignoreTrailing)) {
            return StringUtils.stripEnd(line, ignoreTrailing);
        }
        return line;
    }
}
