package com.reqlint.core.surefire;

import com.reqlint.core.surefire.report.TestRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SurefireTestReportTest {
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private SurefireTestReport underTest;

    @BeforeEach
    void setUp() {
        underTest = new SurefireTestReport();
        Assertions.fail("Do your testing");
    }

    /*
    @Test
    public void can_find_a_test_report_when_its_identifier_is_at_the_begining() {
        String testCaseIdentifier = "TC-401";
        TestRun test = new TestRun(
                TIMESTAMP,
                testCaseIdentifier + " - Whatever",
                "output",
                "failure");
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);
    }

    @Test
    public void can_find_a_test_report_when_its_identifier_is_in_the_middle() {
        String testCaseIdentifier = "TC-401";
        TestRun test = new TestRun(
                TIMESTAMP,
                "Whatever " + testCaseIdentifier + " - Whatever",
                "output",
                "failure");
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);

    }

    @Test
    public void can_find_a_test_report_when_its_identifier_is_at_the_end() {
        String testCaseIdentifier = "TC-401";
        TestRun test = new TestRun(
                TIMESTAMP,
                "Whatever " + testCaseIdentifier,
                "output",
                "failure");
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);

    }

    @Test
    public void can_avoid_identifiers_that_are_substrings() {
        String testCaseIdentifier = "TC-401";
        TestRun test = new TestRun(
                TIMESTAMP,
                "Whatever " + testCaseIdentifier + "01",
                "output",
                "failure");
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier)).isEmpty();
    }

    @Test
    public void can_accept_hyphen_instead_of_underscore() {
        TestRun test = new TestRun(
                TIMESTAMP,
                "TC_401 - Whatever ",
                "output",
                "failure");
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase("TC-401")).containsExactly(test);
        Assertions.assertThat(underTest.reportsOfTestCase("TC_401")).containsExactly(test);
    }
    */
}