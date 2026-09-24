package com.reqlint.core.model.testreport.warnings;

import com.reqlint.core.model.specification.items.TestCase;

/**
 * Found a test case with multiple test reports.
 */
public class MultipleTestReportsForTestCase extends TestRunWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public MultipleTestReportsForTestCase(TestCase testCase, int n) {
        super(testCase, "Test case " + testCase.identifier() + " has " + n + " test reports.");
    }
}
