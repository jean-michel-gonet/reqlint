package com.reqlint.core.surefire;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    public void can_load_reports() {
        Assertions.assertThat(testReport.reports()).isNotEmpty();
    }

    @Test
    public void can_look_for_one_report_by_its_identifier() {
        List<SurefireTestReportItem> reports = testReport.reportsOfTestCase("TC_101");
        Assertions.assertThat(reports).hasSize(1);
        Assertions.assertThat(reports.getFirst().name()).startsWith("@TC_101 Upon first connection");
        Assertions.assertThat(reports.getFirst().isFailed()).isFalse();
    }

    @Test
    public void can_look_for_whole_words() {
        List<SurefireTestReportItem> reports = testReport.reportsOfTestCase("TC_10");
        Assertions.assertThat(reports).isEmpty();
    }

    @Test
    public void can_identify_failed_tests() {
        List<SurefireTestReportItem> reports = testReport.reportsOfTestCase("TC_102");
        Assertions.assertThat(reports).hasSize(1);
        Assertions.assertThat(reports.getFirst().isFailed()).isTrue();
    }

    @Test
    public void can_segregate_output_per_stage() {
        List<SurefireTestReportItem> reports = testReport.reportsOfTestCase("TC_201");
        Assertions.assertThat(reports).hasSize(1);
        SurefireTestReportItem report = reports.getFirst();
        Assertions.assertThat(report.stageOutputs()).hasSize(3);
        Assertions.assertThat(report.stageOutputs().get(0).stageNumber()).isEqualTo(0);
        Assertions.assertThat(report.stageOutputs().get(0).stageTitle()).isEqualTo("Preparation");
        Assertions.assertThat(report.stageOutputs().get(1).stageNumber()).isEqualTo(1);
        Assertions.assertThat(report.stageOutputs().get(1).stageTitle()).isEqualTo("Consume some tokens.");
        Assertions.assertThat(report.stageOutputs().get(2).stageNumber()).isEqualTo(2);
        Assertions.assertThat(report.stageOutputs().get(2).stageTitle()).isEqualTo("Verify that token availability increased according to replenishment rate");
    }

}