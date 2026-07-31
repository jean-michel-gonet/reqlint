package com.reqlint.core.specification.items;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SoftwareRequirementTest {
    private SoftwareRequirement underTest;

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new SoftwareRequirement();
    }

    @Test
    public void can_attach_test_cases() {
        TestCase testCase = new TestCase();
        underTest.attach(testCase);
        Assertions.assertThat(underTest.testCases()).contains(testCase);
    }
}
