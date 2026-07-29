package com.reqlint.core.latex.output;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.items.TestCase;
import com.reqlint.core.surefire.SurefireTestReport;
import com.reqlint.core.surefire.TestReportsLoader;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TestResultsOutput {
    private static final String CRLF = "\r\n";
    private final SpecificationTree specificationTree;
    private final TestReportsLoader reports;

    public TestResultsOutput(SpecificationTree specificationTree, TestReportsLoader reports) {
        this.specificationTree = specificationTree;
        this.reports = reports;
    }

    public void writeTestResults(Writer writer) throws IOException {
        for (TestCase testCase : specificationTree.testCases()) {
            String identifier = testCase.identifier();
            List<SurefireTestReport> matchingReports = reports.reportsOfTestCase(identifier);
            if (matchingReports.isEmpty()) {
                writeTestResultForANonTestedTestCase(writer, testCase);
            } else {
                writeTestResultForATestedTestCase(writer, testCase, matchingReports.getFirst());
            }
        }
    }

    private void writeTestResultForATestedTestCase(Writer writer, TestCase testCase, SurefireTestReport report) {
        if (testCase.testCaseProcedure() == null) {

        } else {

        }
    }

    public void writeTestResultForANonTestedTestCase(Writer writer, TestCase testCase) throws IOException {
        writer.write(String.format("\\subsection{Test results for %s}\r\n", testCase.identifier()));
        writer.write(String.format("\\label{subsec:result-%s}\r\n", testCase.identifier()));
        writer.write(CRLF);
        writer.write("No tests found for this test case." + CRLF);

    }

}
