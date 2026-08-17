package com.reqlint.core.input.surefire;

import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class TestReportsLoaderSurefire {
    private TestReport testReport;

    @BeforeEach
    void setUp() throws IOException {
        SurefireReportsLoader underTest = new SurefireReportsLoader();
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
    }

    @org.junit.jupiter.api.Test
    public void can_look_for_whole_words() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_10");
        Assertions.assertThat(reports).isEmpty();
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