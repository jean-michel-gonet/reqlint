package com.reqlint.core.input.latex.loader;

import com.reqlint.core.input.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.model.specification.SpecificationTree;
import com.reqlint.core.model.specification.TraceabilityMatrixItem;
import com.reqlint.core.model.specification.items.EquipmentRequirement;
import com.reqlint.core.model.specification.items.SoftwareRequirement;
import com.reqlint.core.model.specification.items.TestCase;
import com.reqlint.core.testutils.TextFileContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

class SpecificationTreeLoaderTest {

    private static final String ER_ID = "ER_ID";
    private static final String SR_ID = "SR_ID";
    private static final String TC_ID = "TC_ID";

    private static final String ER_TITLE = "ER";
    private static final String SR_TITLE = "SR";
    private static final String TC_TITLE = "TC";

    private static final String STAGE_1 = "Stage 1";
    private static final String STAGE_2 = "Stage 2";

    @TempDir
    private File temporaryFolder;

    private SpecificationTreeLoader underTest;
    private File root;
    private SpecificationTree specificationTree;

    @BeforeEach
    void setUp() {
        root = new File(temporaryFolder, "root.tex");
        underTest = new SpecificationTreeLoader();
        specificationTree = underTest.specificationTree();
    }

    @Test
    public void can_load_a_tree() throws Exception {

        TextFileContent.createFileWithLines(root, List.of(
                String.format("\\begin{equipmentrequirement}{%s}{%s}", ER_ID, ER_TITLE),
                TextFileContent.CRLF,
                "\\end{equipmentrequirement}",
                TextFileContent.CRLF,
                "Text in between can be safely ignored.",
                TextFileContent.CRLF,
                "Because, who cares.",
                TextFileContent.CRLF,
                String.format("\\begin{softwarerequirement}{%s}{%s}", SR_ID, SR_TITLE),
                TextFileContent.CRLF,
                String.format("\\childof{%s}", ER_ID),
                TextFileContent.CRLF,
                "\\end{softwarerequirement}",
                TextFileContent.CRLF,
                String.format("\\begin{testcase}{%s}{%s}", TC_ID, TC_TITLE),
                TextFileContent.CRLF,
                String.format("\\childof{%s}", SR_ID),
                TextFileContent.CRLF,
                "\\begin{testprocedure}",
                TextFileContent.CRLF,
                String.format("  \\stage {%s}", STAGE_1),
                TextFileContent.CRLF,
                String.format("  \\stage {%s}", STAGE_2),
                TextFileContent.CRLF,
                "\\end{testprocedure}",
                TextFileContent.CRLF,
                "\\end{testcase}"
        ));

        try (Reader reader = new InputStreamReader(new FileInputStream(root))) {
            underTest.load(reader);
        }

        specificationTree.verify();
        List<TraceabilityMatrixItem> traceabilityMatrix = specificationTree.buildTraceabilityMatrix();

        Assertions.assertThat(traceabilityMatrix).containsExactly(
                new TraceabilityMatrixItem(
                        new EquipmentRequirement(ER_ID, ER_TITLE),
                        new SoftwareRequirement(SR_ID, SR_TITLE),
                        new TestCase(TC_ID, TC_TITLE)));
    }

    @Test
    public void can_load_a_tree_from_file_without_breaks() throws Exception {
        TextFileContent.createFileWithLines(root, List.of(
                String.format("\\begin{equipmentrequirement}{%s}{%s}", ER_ID, ER_TITLE),
                "\\end{equipmentrequirement}",
                String.format("\\begin{softwarerequirement}{%s}{%s}", SR_ID, SR_TITLE),
                String.format("\\childof{%s}", ER_ID),
                "\\end{softwarerequirement}",
                String.format("\\begin{testcase}{%s}{%s}", TC_ID, TC_TITLE),
                String.format("\\childof{%s}", SR_ID),
                "\\begin{testprocedure}",
                String.format("  \\stage {%s}", STAGE_1),
                String.format("  \\stage {%s}", STAGE_2),
                "\\end{testprocedure}",
                "\\end{testcase}"
        ));

        try (Reader reader = new InputStreamReader(new FileInputStream(root))) {
            underTest.load(reader);
        }

        specificationTree.verify();
        List<TraceabilityMatrixItem> traceabilityMatrix = specificationTree.buildTraceabilityMatrix();

        Assertions.assertThat(traceabilityMatrix).containsExactly(
                new TraceabilityMatrixItem(
                        new EquipmentRequirement(ER_ID, ER_TITLE),
                        new SoftwareRequirement(SR_ID, SR_TITLE),
                        new TestCase(TC_ID, TC_TITLE)));
    }
}