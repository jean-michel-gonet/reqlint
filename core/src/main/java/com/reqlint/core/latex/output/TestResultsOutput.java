package com.reqlint.core.latex.output;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.items.TestCase;
import com.reqlint.core.latex.loader.items.TestProcedure;
import com.reqlint.core.latex.loader.items.TestStage;
import com.reqlint.core.latex.loader.warnings.SpecificationTreeWarning;
import com.reqlint.core.surefire.SurefireStageOutputItem;
import com.reqlint.core.surefire.SurefireTestReport;
import com.reqlint.core.surefire.SurefireTestReportItem;
import com.reqlint.core.surefire.warnings.*;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

public class TestResultsOutput {
    private static final String CRLF = "\r\n";
    private final SpecificationTree specificationTree;
    private final SurefireTestReport testReport;

    public TestResultsOutput(SpecificationTree specificationTree, SurefireTestReport testReport) {
        this.specificationTree = specificationTree;
        this.testReport = testReport;
    }

    public void writeTestResults(Writer writer) throws IOException {
        for (TestCase testCase : specificationTree.testCases()) {
            // Always the title:
            writeTitle(writer, testCase);

            // Look for the corresponding test report:
            String identifier = testCase.identifier();
            List<SurefireTestReportItem> matchingTestReports = testReport.reportsOfTestCase(identifier);

            // Add the description if there is no test report:
            if (matchingTestReports.isEmpty()) {
                writeTestResultForANonTestedTestCase(writer, testCase);
                continue;
            }

            // Add the description if there are test reports:
            writeTestResultForATestedTestCase(writer, testCase, matchingTestReports);
        }
    }

    public void writeTestWarnings(Writer writer) throws IOException {
        writer.write("\\begin{itemize}\r\n");
        if (testReport.warnings().isEmpty()) {
            writer.write("    \\item No warnings detected.\r\n");
        } else {
            for (TestReportWarning warning : testReport.warnings()) {
                writer.write(String.format("    \\item \\nameref{%s} -- %s\r\n",
                        warning.testCase().identifier(),
                        warning.description()));
            }
        }
        writer.write("\\end{itemize}\r\n");
    }

    private void writeTitle(Writer writer, TestCase testCase) throws IOException {
        writer.write(String.format("\\subsection{Test results for %s}\r\n", testCase.identifier()));
        writer.write(String.format("\\label{subsec:result-%s}\r\n", testCase.identifier()));
        writer.write(String.format("\\childof{%s}\r\n", testCase.identifier()));
        writer.write(CRLF);
    }

    private void writeTestResultForANonTestedTestCase(Writer writer, TestCase testCase) throws IOException {
        writer.write("No tests found for this test case." + CRLF);
        testReport.addReportWarning(new NoTestReportsForTestCase(testCase));
    }

    private void writeTestResultForATestedTestCase(Writer writer, TestCase testCase, List<SurefireTestReportItem> matchingTestReports) throws IOException {
        if (matchingTestReports.size() > 1) {
            testReport.addReportWarning(new MultipleTestReportsForTestCase(testCase, matchingTestReports.size()));
        }
        SurefireTestReportItem testReportItem = matchingTestReports.getFirst();

        writeTestResult(writer, testReportItem);
        checkConsistency(testCase, testReportItem);
    }

    private void checkConsistency(TestCase testCase, SurefireTestReportItem testReportItem) {
        if (testReportItem.isFailed()) {
            testReport.addReportWarning(new TestFailed(testCase));
            return;
        }
        TestProcedure testProcedure = testCase.testProcedure();
        if (testProcedure == null) {
            return;
        }

        if (testProcedure.stages().size() != testReportItem.stageOutputs().size()) {
            testReport.addReportWarning(new MismatchedNumberOfStages(testCase, testReportItem.stageOutputs().size()));
            return;
        }

        for(int n = 1; n < testReportItem.stageOutputs().size(); n++) {
            TestStage testStage = testProcedure.stages().get(n - 1);
            SurefireStageOutputItem stageOutputItem = testReportItem.stageOutputs().get(n);
            if (!Objects.equals(testStage.description(), stageOutputItem.stageTitle())) {
                testReport.addReportWarning(new MismatchedStageDescription(testCase, n));
                return;
            }
        }
    }

    private void writeTestResult(Writer writer, SurefireTestReportItem testReportItem) throws IOException {
        for (SurefireStageOutputItem stageOutput : testReportItem.stageOutputs()) {
            writer.write(String.format("\\stagetitle{Stage %d: %s}\r\n", stageOutput.stageNumber(), stageOutput.stageTitle()));
            writer.write("\\begin{lstlisting}[style=stageLog]\r\n");
            writer.write(stageOutput.stageOutput());
            writer.write("\\end{lstlisting}\r\n");
        }
        if (testReportItem.isFailed()) {
            writer.write("\\testfailure{Test failed}\r\n");
            writer.write("\\begin{lstlisting}[style=stageLog]\r\n");
            writer.write(testReportItem.failure());
            writer.write("\\end{lstlisting}\r\n");
        }
    }
}
