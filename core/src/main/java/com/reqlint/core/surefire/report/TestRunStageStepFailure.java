package com.reqlint.core.surefire.report;

/**
 * Describes a failure that occurred in a {@link TestRunStageStep}
 * @param type The failure type.
 * @param output The associated log.
 */
public record TestRunStageStepFailure(String type, String output) {
}
