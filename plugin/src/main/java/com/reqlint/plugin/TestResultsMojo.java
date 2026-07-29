package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.output.TestResultsOutput;
import com.reqlint.core.latex.reader.LatexReader;
import com.reqlint.core.surefire.SurefireTestReport;
import com.reqlint.core.surefire.TestReportsFinder;
import com.reqlint.core.surefire.TestReportsLoader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mojo(name = "test-results", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class TestResultsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    private List<MavenProject> reactorProjects;

    @Parameter(name = "basePath", defaultValue = "${project.build.directory}")
    private String basePath;

    @Parameter(name = "inputFilePath")
    private String inputFilePath;

    @Parameter(name = "testResultsFilePath")
    private String testResultsFilePath;

    @Parameter(name = "testWarningsFilePath")
    private String testWarningsFilePath;


    @Override
    public void execute() throws MojoExecutionException {
        this.getLog();
        try {
            getLog().info("Load specification tree");
            SpecificationTree specificationTree = readSpecificationTree();
            getLog().info("Load surefire reports");
            SurefireTestReport surefireTestReport = readSurefireTestReport();
            TestResultsOutput testResultsOutput = new TestResultsOutput(specificationTree, surefireTestReport);
            getLog().info("Writing test results output");
            writeTestResults(testResultsOutput);
            getLog().info("Writing test warnings output");
            writeTestWarnings(testResultsOutput);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run traceability-matrix goal", e);
        }
    }

    private void writeTestResults(TestResultsOutput testResultsOutput) throws IOException {
        File testsResultsFile = new File(new File(basePath), testResultsFilePath);
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(testsResultsFile), StandardCharsets.UTF_8)) {
            testResultsOutput.writeTestResults(writer);
        }
    }

    private void writeTestWarnings(TestResultsOutput testResultsOutput) throws IOException {
        File testsResultsFile = new File(new File(basePath), testWarningsFilePath);
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(testsResultsFile), StandardCharsets.UTF_8)) {
            testResultsOutput.writeTestWarnings(writer);
        }
    }

    private SurefireTestReport readSurefireTestReport() throws IOException {

        List<File> surefireReportsFolders = reactorProjects.stream()
                .map(project -> new File(project.getBuild().getDirectory(), "surefire-reports"))
                .filter(File::exists)
                .filter(File::isDirectory)
                .toList();

        TestReportsLoader testReportsLoader = new TestReportsLoader();
        for (File surefireReportsFolder : surefireReportsFolders) {
            getLog().info("Loading Surefire reports from " + surefireReportsFolder.getAbsolutePath());
            TestReportsFinder testReportsFinder = new TestReportsFinder(surefireReportsFolder);
            for (File surefireTestReport : testReportsFinder.search()) {
                testReportsLoader.loadReport(surefireTestReport);
            }
        }

        SurefireTestReport testReport = testReportsLoader.getTestReport();
        getLog().info("Loaded " + testReport.numberOfReports() + " reports");
        return testReportsLoader.getTestReport();
    }

    private SpecificationTree readSpecificationTree() throws IOException {
        File inputFile = new File(new File(basePath), inputFilePath);
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException(inputFile.getAbsolutePath() + " does not exist or is not a file");
        }

        LatexReader latexReader = new LatexReader(inputFile);
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
        return specificationTreeLoader.load();
    }
}
