package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.output.TestResultsOutput;
import com.reqlint.core.latex.parser.LatexReader;
import com.reqlint.core.specification.SpecificationTree;
import com.reqlint.core.surefire.SurefireTestReport;
import com.reqlint.core.surefire.TestReportsFinder;
import com.reqlint.core.surefire.TestReportsLoader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mojo(name = "test-results", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class TestResultsMojo extends ReqlintMojo {

    @Override
    public void execute() throws MojoExecutionException {
        this.getLog();
        try {
            getLog().info("Load specification tree");
            SpecificationTree specificationTree = readSpecificationTree();
            getLog().info("Loading surefire test reports");
            SurefireTestReport surefireTestReport = readSurefireTestReport();
            TestResultsOutput testResultsOutput = new TestResultsOutput(specificationTree, surefireTestReport);
            getLog().info("Writing test results output");
            writeTestResults(testResultsOutput);
            getLog().info("Writing test warnings output");
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

    private SurefireTestReport readSurefireTestReport() throws IOException {

        List<File> surefireReportsFolders = allProjects.stream()
                .map(project -> new File(project.getBuild().getDirectory(), "surefire-reports"))
                .filter(File::exists)
                .filter(File::isDirectory)
                .toList();

        TestReportsLoader testReportsLoader = new TestReportsLoader();
        for (File surefireReportsFolder : surefireReportsFolders) {
            getLog().info("Loading surefire test reports from " + surefireReportsFolder.getAbsolutePath());
            TestReportsFinder testReportsFinder = new TestReportsFinder(surefireReportsFolder);
            for (File surefireTestReport : testReportsFinder.search()) {
                testReportsLoader.loadReport(surefireTestReport);
            }
        }

        SurefireTestReport testReport = testReportsLoader.getTestReport();
        getLog().info("Loaded " + testReport.numberOfReports() + " surefire test reports");
        return testReportsLoader.getTestReport();
    }

    private SpecificationTree readSpecificationTree() throws IOException {
        if (!specificationLatexDocument.isFile()) {
            throw new IllegalArgumentException(specificationLatexDocument.getAbsolutePath() + " does not exist or is not a file");
        }

        LatexReader latexReader = new LatexReader(specificationLatexDocument);
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
        return specificationTreeLoader.load();
    }
}
