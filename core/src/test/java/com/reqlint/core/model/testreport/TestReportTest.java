package com.reqlint.core.model.testreport;

import com.reqlint.core.model.testreport.items.TestRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TestReportTest {
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private TestReport underTest;

    @BeforeEach
    void setUp() {
        underTest = new TestReport();
    }

    @Test
    public void can_find_a_test_report_when_its_identifier_is_at_the_begining() {
        String testCaseIdentifier = "TC-401";
        TestRun test = TestRun.builder()
                .timeStamp(TIMESTAMP)
                .name(testCaseIdentifier + " - Whatever")
                .build();
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);
    }

    @Test
    public void can_find_a_test_report_when_its_identifier_is_in_the_middle() {
        String testCaseIdentifier = "TC-401";
        TestRun test = TestRun.builder()
                .timeStamp(TIMESTAMP)
                .name("Whatever " + testCaseIdentifier + " - Whatever")
                .build();
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);
    }

    @Test
    public void can_find_a_test_report_when_its_identifier_is_at_the_end() {
        String testCaseIdentifier = "TC-401";
        TestRun test = TestRun.builder()
                .timeStamp(TIMESTAMP)
                .name("Whatever " + testCaseIdentifier)
                .build();
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier))
                .contains(test);
    }

    @Test
    public void can_avoid_identifiers_that_are_substrings() {
        String testCaseIdentifier = "TC-401";
        TestRun test = TestRun.builder()
                .timeStamp(TIMESTAMP)
                .name("Whatever " + testCaseIdentifier + "01")
                .build();
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase(testCaseIdentifier)).isEmpty();
    }

    @Test
    public void can_accept_hyphen_instead_of_underscore() {
        TestRun test = TestRun.builder()
                .timeStamp(TIMESTAMP)
                .name("TC_401 - Whatever ")
                .build();
        underTest.addReportItem(test);

        Assertions.assertThat(underTest.reportsOfTestCase("TC-401")).containsExactly(test);
        Assertions.assertThat(underTest.reportsOfTestCase("TC_401")).containsExactly(test);
    }
}