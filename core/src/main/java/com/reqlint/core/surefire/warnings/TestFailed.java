package com.reqlint.core.surefire.warnings;

import com.reqlint.core.latex.loader.items.TestCase;

/**
 * Found a failed test case.
 */
public class TestFailed extends TestReportWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public TestFailed(TestCase testCase) {
        super(testCase, "Test case " + testCase.identifier() + " has failed.");
    }
}
