package com.reqlint.core.surefire.warnings;

import com.reqlint.core.specification.items.TestCase;

/**
 * A stage in the test procedure has a different description than its corresponding stage in the test report.
 */
public class MismatchedStageDescription extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public MismatchedStageDescription(TestCase testCase, int stagePosition) {
        super(testCase, String.format(
                "Stage %d in the test procedure of test case %s has a different description than in test report.",
                stagePosition,
                testCase.identifier()));
    }
}
