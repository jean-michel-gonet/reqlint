package com.reqlint.core.surefire;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class TestReportsLoaderTest {
    private TestReportsLoader underTest;

    @BeforeEach
    void setUp() {
        underTest = new TestReportsLoader();
    }

    @Test
    public void can_load_reports() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/surefire-reports/TEST-com.reqlint.sandbox.cucumber.CucumberIntegrationTest.xml");
        underTest.loadReport(is);
        Assertions.assertThat(underTest.reports()).isNotEmpty();
    }
}