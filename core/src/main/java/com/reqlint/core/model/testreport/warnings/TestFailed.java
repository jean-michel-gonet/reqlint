package com.reqlint.core.model.testreport.warnings;

import com.reqlint.core.model.specification.items.TestCase;

/**
 * Found a failed test case.
 */
public class TestFailed extends TestRunWarning {
    /**
     * Class constructor.
     * @param testCase The concerned test case.
     */
    public TestFailed(TestCase testCase) {
        super(testCase, "Test case " + testCase.identifier() + " has failed.");
    }
}
