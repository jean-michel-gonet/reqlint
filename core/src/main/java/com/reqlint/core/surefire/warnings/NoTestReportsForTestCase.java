package com.reqlint.core.surefire.warnings;

import com.reqlint.core.specification.items.TestCase;

/**
 * Found a test case without corresponding test report.
 */
public class NoTestReportsForTestCase extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public NoTestReportsForTestCase(TestCase testCase) {
        super(testCase, "Test case " + testCase.identifier() + " has no test report.");
    }
}
