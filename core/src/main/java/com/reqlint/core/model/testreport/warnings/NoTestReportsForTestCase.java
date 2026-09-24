package com.reqlint.core.model.testreport.warnings;

import com.reqlint.core.model.specification.items.TestCase;

/**
 * Found a test case without corresponding test report.
 */
public class NoTestReportsForTestCase extends TestRunWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public NoTestReportsForTestCase(TestCase testCase) {
        super(testCase, "Test case " + testCase.identifier() + " has no test report.");
    }
}
