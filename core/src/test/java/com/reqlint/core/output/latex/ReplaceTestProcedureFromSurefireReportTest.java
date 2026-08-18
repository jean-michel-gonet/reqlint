package com.reqlint.core.output.latex;

import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.model.testreport.items.TestRunStage;
import com.reqlint.core.model.testreport.items.TestRunStageStep;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static com.reqlint.core.testutils.TextFileContent.createFileWithLines;
import static com.reqlint.core.testutils.TextFileContent.readLinesFromFile;

class ReplaceTestProcedureFromSurefireReportTest {
    private static final String TEST_CASE_ID = "TC-1101";
    private static final String CRLF = "\r\n";

    @TempDir
    private File temporaryFolder;

    private ReplaceTestProcedureFromSurefireReport underTest;

    private TestReport testReport;
    private File rootFile;

    @BeforeEach
    void setUp() {
        testReport = new TestReport();
        underTest = new ReplaceTestProcedureFromSurefireReport(testReport);
        rootFile = new File(temporaryFolder, "root.tex");
    }

    @Test
    public void can_replace() throws Exception {
        File file = new File(temporaryFolder, "/subfolder/one.tex");
        createFileWithLines(file, List.of(
                String.format("\\begin{testcase}{%s}{A nice test case}", TEST_CASE_ID),
                CRLF,
                "\\testprocedurefromsurefirereport",
                CRLF,
                "\\end{testcase}"));

        createFileWithLines(rootFile, List.of(
                "\\import{subfolder/}{one}",
                CRLF));

        testReport.addReportItem(TestRun.builder()
                        .name(TEST_CASE_ID)
                        .preparation(TestRunStage.builder()
                                .addOperation(TestRunStageStep.builder()
                                        .ordinal(1)
                                        .title("XXX")
                                        .appendToOutput("O1")
                                        .appendToOutput("O2")
                                        .build())
                                .build())
                        .addStage(TestRunStage.builder()
                                .ordinal(1)
                                .title("First stage")
                                .addOperation(TestRunStageStep.builder()
                                        .ordinal(1)
                                        .title("YYY")
                                        .appendToOutput("UU")
                                        .appendToOutput("VV")
                                        .build())
                                .addOperation(TestRunStageStep.builder()
                                        .ordinal(2)
                                        .title("ZZZ")
                                        .appendToOutput("AA")
                                        .appendToOutput("BB")
                                        .build())
                                .build())
                .build());

        underTest.doIt(rootFile);

        Assertions.assertThat(readLinesFromFile(file))
                .containsExactly(
                        "\\begin{testcase}{TC-1101}{A nice test case}",
                        "\\begin{itemize}",
                        "\\item \\textbf{Preparation}",
                        "\\begin{enumerate}",
                        "\\item XXX",
                        "\\end{enumerate}",
                        "\\item \\textbf{Stage 1} -- First stage",
                        "\\begin{itemize}",
                        "\\item \\textbf{Operations}",
                        "\\begin{enumerate}",
                        "\\item YYY",
                        "\\item ZZZ",
                        "\\end{enumerate}",
                        "\\end{itemize}",
                        "\\end{itemize}",
                        "",
                        "\\end{testcase}");

    }

}