package com.reqlint.core.output.latex;

import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.model.testreport.items.TestRunStage;
import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.model.testreport.items.TestRunStageStep;
import com.reqlint.core.testutils.TextFileContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

class TestResultsOutputTest {
    private static final String TITLE = "TITLE";
    private static final String IDENTIFIER = "TC100";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final int STAGE_1_NUMBER = 1;
    private static final String STAGE_1_TITLE = "The first stage";
    private static final String STAGE_1_OUTPUT = "The output of the first stage";

    private static final String NO_FAILURE = "";
    private static final String FAILURE = "Failure";


    private static final String TEST_RESULT_FILENAME = "test-results.tex";
    private static final String TEST_WARNINGS_FILENAME = "test-warnings.tex";

    private TestResultsOutput underTest;

    @TempDir
    public File temporaryFolder;

    private SpecificationTree specificationTree;
    private TestReport testReport;
    private File testResultsFile;
    private File testWarningsFile;

    @BeforeEach
    void setUp() {
        specificationTree = new SpecificationTree();
        testReport = new TestReport();

        underTest = new TestResultsOutput(specificationTree, testReport);

        testResultsFile = new File(temporaryFolder, TEST_RESULT_FILENAME);
        testWarningsFile = new File(temporaryFolder, TEST_WARNINGS_FILENAME);
    }

    @Test
    public void can_output_the_test_result() throws Exception {
        TestCase testCase = new TestCase(IDENTIFIER, TITLE);
        specificationTree.attach(testCase);

        testReport.addReportItem(new TestRun(
                TIMESTAMP,
                IDENTIFIER,
                null,
                List.of(TestRunStage.builder()
                                .ordinal(STAGE_1_NUMBER)
                                .title(STAGE_1_TITLE)
                                .addOperation(TestRunStageStep.builder()
                                        .ordinal(0)
                                        .title("XX")
                                        .appendToOutput(STAGE_1_OUTPUT)
                                        .build())
                                .build())));

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(testResultsFile));
        underTest.writeTestResults(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(testResultsFile));

        Assertions.assertThat(lines).containsExactly(
                "\\subsection{Test results for TC100}",
                "\\label{subsec:result-TC100}",
                "\\childof{TC100}",
                "",
                "\\begin{itemize}",
                "    \\item  \\textbf{Stage 1} -- The first stage",
                "    \\begin{itemize}",
                "        \\item \\textbf{Operations}",
                "        \\begin{enumerate}",
                "             \\item  XX",
                "        \\end{enumerate}",
                "    \\end{itemize}",
                "\\end{itemize}");
    }

    @Test
    public void can_output_the_test_result_when_there_is_none() throws Exception {
        TestCase testCase = new TestCase(IDENTIFIER, TITLE);
        specificationTree.attach(testCase);

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(testResultsFile));
        underTest.writeTestResults(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(testResultsFile));

        Assertions.assertThat(lines).containsExactly(
                "\\subsection{Test results for TC100}",
                "\\label{subsec:result-TC100}",
                "\\childof{TC100}",
                "",
                "No tests found for this test case.");
    }

}