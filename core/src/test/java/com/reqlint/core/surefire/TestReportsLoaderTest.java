package com.reqlint.core.surefire;

import com.reqlint.core.surefire.report.TestRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class TestReportsLoaderTest {
    private TestReportsLoader underTest;
    private SurefireTestReport testReport;

    @BeforeEach
    void setUp() throws IOException {
        underTest = new TestReportsLoader();
        InputStream is = this.getClass().getResourceAsStream("/surefire-reports/TEST-com.reqlint.sandbox.cucumber.CucumberIntegrationTest.xml");
        underTest.loadReport(is);
        testReport = underTest.getTestReport();
    }

    @org.junit.jupiter.api.Test
    public void can_load_reports() {
        Assertions.assertThat(testReport.reports()).isNotEmpty();
    }

    @org.junit.jupiter.api.Test
    public void can_look_for_one_report_by_its_identifier() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_101");
        Assertions.assertThat(reports).hasSize(1);
        Assertions.assertThat(reports.getFirst().title()).startsWith("The bit bucket works @TC_101 Upon first connection");
        Assertions.assertThat(reports.getFirst().isFailed()).isFalse();
    }

    @org.junit.jupiter.api.Test
    public void can_look_for_whole_words() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_10");
        Assertions.assertThat(reports).isEmpty();
    }

    @org.junit.jupiter.api.Test
    public void can_identify_failed_tests() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_102");
        Assertions.assertThat(reports).hasSize(1);
        Assertions.assertThat(reports.getFirst().isFailed()).isTrue();
    }

    @org.junit.jupiter.api.Test
    public void can_segregate_output_per_stage() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_201");
        Assertions.assertThat(reports).hasSize(1);
        TestRun report = reports.getFirst();
        Assertions.assertThat(report.stages()).hasSize(3);
        Assertions.assertThat(report.stages().get(0).ordinal()).isEqualTo(0);
        Assertions.assertThat(report.stages().get(0).title()).isEqualTo("Preparation");
        Assertions.assertThat(report.stages().get(1).ordinal()).isEqualTo(1);
        Assertions.assertThat(report.stages().get(1).title()).isEqualTo("Consume some tokens.");
        Assertions.assertThat(report.stages().get(2).ordinal()).isEqualTo(2);
        Assertions.assertThat(report.stages().get(2).title()).isEqualTo("Verify that token availability increased according to replenishment rate");
    }

}