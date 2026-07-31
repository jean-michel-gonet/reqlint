package com.reqlint.core.surefire.warnings;

import com.reqlint.core.specification.items.TestCase;

/**
 * Found a test case with multiple test reports.
 */
public class MultipleTestReportsForTestCase extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public MultipleTestReportsForTestCase(TestCase testCase, int n) {
        super(testCase, "Test case " + testCase.identifier() + " has " + n + " test reports.");
    }
}
