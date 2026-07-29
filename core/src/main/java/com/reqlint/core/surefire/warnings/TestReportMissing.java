package com.reqlint.core.surefire.warnings;

import com.reqlint.core.latex.loader.items.TestCase;

/**
 * Found a test case without corresponding test report.
 */
public class TestReportMissing extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    protected TestReportMissing(TestCase testCase) {
        super(testCase, "Test case " + testCase.identifier() + " has no test report.");
    }
}
