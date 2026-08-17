package com.reqlint.plugin;

import com.reqlint.core.output.latex.TestResultsOutput;
import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.input.surefire.SurefireReportsFinder;
import com.reqlint.core.input.surefire.SurefireReportsLoader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mojo(name = "test-results", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class TestResultsMojo extends ReqlintMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultsMojo.class);

    @Override
    public void execute() throws MojoExecutionException {
        LOGGER.info("test-result goal started");
        try {
            LOGGER.info("Load specification tree");
            SpecificationTree specificationTree = readSpecificationTree();
            LOGGER.info("Loading surefire test reports");
            TestReport testReport = readSurefireTestReport();
            TestResultsOutput testResultsOutput = new TestResultsOutput(specificationTree, testReport);
            LOGGER.info("Writing test results output");
            writeTestResults(testResultsOutput);
            LOGGER.info("Writing test warnings output");
            writeTestWarnings(testResultsOutput);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run test-result goal", e);
        }
    }

    private void writeTestResults(TestResultsOutput testResultsOutput) throws IOException {
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(this.testResultsOutput), StandardCharsets.UTF_8)) {
            testResultsOutput.writeTestResults(writer);
        }
    }

    private void writeTestWarnings(TestResultsOutput testResultsOutput) throws IOException {
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(testWarningsOutput), StandardCharsets.UTF_8)) {
            testResultsOutput.writeTestWarnings(writer);
        }
    }

    private TestReport readSurefireTestReport() throws IOException {

        List<File> surefireReportsFolders = session.getAllProjects().stream()
                .map(project -> new File(project.getBuild().getDirectory(), "surefire-reports"))
                .filter(File::exists)
                .filter(File::isDirectory)
                .toList();

        SurefireReportsLoader surefireReportsLoader = new SurefireReportsLoader();
        for (File surefireReportsFolder : surefireReportsFolders) {
            LOGGER.info("Loading surefire test reports from " + surefireReportsFolder.getAbsolutePath());
            SurefireReportsFinder surefireReportsFinder = new SurefireReportsFinder(surefireReportsFolder);
            for (File surefireTestReport : surefireReportsFinder.search()) {
                surefireReportsLoader.loadReport(surefireTestReport);
            }
        }

        TestReport testReport = surefireReportsLoader.getTestReport();
        LOGGER.info("Loaded " + testReport.numberOfReports() + " surefire test reports");
        return surefireReportsLoader.getTestReport();
    }
}
