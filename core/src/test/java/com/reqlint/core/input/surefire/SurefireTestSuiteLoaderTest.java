package com.reqlint.core.input.surefire;

import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class SurefireTestSuiteLoaderTest {
    private TestReport testReport;

    @BeforeEach
    void setUp() throws IOException {
        SurefireTestSuiteLoader underTest = new SurefireTestSuiteLoader();
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
        List<TestRun> reports = testReport.reportsOfTestCase("TC_101");
        Assertions.assertThat(reports).hasSize(1);
        Assertions.assertThat(reports.getFirst().title()).startsWith("The bit bucket works @TC-101 Upon first connection");
    }

    @Test
    public void can_look_for_whole_words() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_10");
        Assertions.assertThat(reports).isEmpty();
    }

    @Test
    public void can_segregate_output_per_stage() {
        List<TestRun> reports = testReport.reportsOfTestCase("TC_201");
        Assertions.assertThat(reports).hasSize(1);
        TestRun report = reports.getFirst();

        Assertions.assertThat(report.preparation()).isNotNull();
        Assertions.assertThat(report.preparation().operations()).hasSize(2);
        Assertions.assertThat(report.stages()).hasSize(2);

        Assertions.assertThat(report.stages().get(0).operations()).hasSize(0);
        Assertions.assertThat(report.stages().get(0).expectations()).hasSize(1);

        Assertions.assertThat(report.stages().get(1).operations()).hasSize(1);
        Assertions.assertThat(report.stages().get(1).expectations()).hasSize(5);
    }
}