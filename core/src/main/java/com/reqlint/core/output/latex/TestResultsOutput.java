package com.reqlint.core.output.latex;

import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.model.specification.items.TestProcedure;
import com.reqlint.core.model.specification.items.TestStage;
import com.reqlint.core.model.testreport.items.TestRunStage;
import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.model.testreport.items.TestRunStageStep;
import com.reqlint.core.model.testreport.warnings.*;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

public class TestResultsOutput {
    private static final String CRLF = "\r\n";
    private final SpecificationTree specificationTree;
    private final TestReport testReport;

    public TestResultsOutput(SpecificationTree specificationTree, TestReport testReport) {
        this.specificationTree = specificationTree;
        this.testReport = testReport;
    }

    public void writeTestResults(Writer writer) throws IOException {
        for (TestCase testCase : specificationTree.testCases()) {
            // Always the title:
            writeTitle(writer, testCase);

            // Look for the corresponding test report:
            String identifier = testCase.identifier();
            List<TestRun> matchingTestReports = testReport.reportsOfTestCase(identifier);

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
            for (TestRunWarning warning : testReport.warnings()) {
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

    private void writeTestResultForATestedTestCase(Writer writer, TestCase testCase, List<TestRun> matchingTestReports) throws IOException {
        if (matchingTestReports.size() > 1) {
            testReport.addReportWarning(new MultipleTestReportsForTestCase(testCase, matchingTestReports.size()));
        }
        TestRun testReportItem = matchingTestReports.getFirst();

        writeTestResult(writer, testReportItem, true);
        checkConsistency(testCase, testReportItem);
    }

    private void checkConsistency(TestCase testCase, TestRun testReportItem) {
        TestProcedure testProcedure = testCase.testProcedure();
        if (testProcedure == null) {
            return;
        }

        if (testProcedure.stages().size() + 1 != testReportItem.stages().size()) {
            testReport.addReportWarning(new MismatchedNumberOfStages(testCase, testReportItem.stages().size()));
            return;
        }

        for(int n = 1; n < testReportItem.stages().size(); n++) {
            TestStage testStage = testProcedure.stages().get(n - 1);
            TestRunStage stageOutputItem = testReportItem.stages().get(n);
            if (!Objects.equals(testStage.description(), stageOutputItem.title())) {
                testReport.addReportWarning(new MismatchedStageDescription(testCase, n));
                return;
            }
        }
    }

    /**
     * Writes one test result to the specified writer.
     * @param writer The writer.
     * @param test The test result.
     * @param withLogs Include or not the logs associated with the test run.
     * @throws IOException Hopefully not
     * TODO: Having this static method looks like a bad design...
     */
    public static void writeTestResult(Writer writer, TestRun test, boolean withLogs) throws IOException {
        if (test.preparation() == null && test.stages().isEmpty()) {
            writer.write("No description for " + test.title() + CRLF);
            return;
        }

        writer.write("\\begin{itemize}\r\n");
        if (test.preparation() != null && !test.preparation().operations().isEmpty()) {
            writer.write("\\item \\textbf{Preparation}\r\n");
            writeStageSteps(writer, test.preparation().operations(), withLogs);
        }
        for (TestRunStage stage : test.stages()) {
            writer.write("\\item \\textbf{Stage " + stage.ordinal() + "} -- " + stage.title() + "\r\n");
            writer.write("\\begin{itemize}\r\n");
            if (!stage.operations().isEmpty()) {
                writer.write("\\item \\textbf{Operations}\r\n");
                writeStageSteps(writer, stage.operations(), withLogs);
            }
            if (!stage.expectations().isEmpty()) {
                writer.write("\\item \\textbf{Expectations}\r\n");
                writeStageSteps(writer, stage.expectations(), withLogs);
            }
            writer.write("\\end{itemize}\r\n");
        }
        writer.write("\\end{itemize}\r\n");
    }

    private static void writeStageSteps(Writer writer, List<TestRunStageStep> stageSteps, boolean withLogs) throws IOException {
        if (stageSteps.isEmpty()) {
            return;
        }
        writer.write("\\begin{enumerate}\r\n");
        for (TestRunStageStep stageStep : stageSteps) {
            writeStageStep(writer, stageStep, withLogs);
        }
        writer.write("\\end{enumerate}\r\n");
    }

    private static void writeStageStep(Writer writer, TestRunStageStep stageStep, boolean withLogs) throws IOException {
        writer.write("\\item " + stageStep.title() + "\r\n");
        if (stageStep.output().isEmpty() || !withLogs) {
            return;
        }

        writer.write("\\begin{lstlisting}[style=stageLog]\r\n");
        for (String s : stageStep.output()) {
            writer.write(s + "\r\n");
        }
        writer.write("\\end{lstlisting}\r\n");

    }
}
