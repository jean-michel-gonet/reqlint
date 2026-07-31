package com.reqlint.core.surefire.warnings;

import com.reqlint.core.specification.items.TestCase;

/**
 * The test procedure of a test case has a different number of stages than found in the surefire test report.
 */
public class MismatchedNumberOfStages extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public MismatchedNumberOfStages(TestCase testCase, int stagesInTestReport) {
        super(testCase, String.format(
                "The test procedure in test case %s has %d stages, but test report has %d stages.",
                testCase.identifier(),
                testCase.testProcedure().stages().size(),
                stagesInTestReport));
    }
}
