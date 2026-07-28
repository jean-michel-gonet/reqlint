package com.reqlint.core.latex.output;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.items.EquipmentRequirement;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.loader.items.TestCase;
import com.reqlint.core.testutils.TextFileContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.List;

class TraceabilityMatrixOutputTest {
    private static final String TRACEABILITY_MATRIX_FILE_NAME = "traceability-matrix.tex";
    @TempDir
    public File temporaryFolder;

    private TraceabilityMatrixOutput underTest;
    private File traceabilityMatrixFile;
    private SpecificationTree specificationTree;

    @BeforeEach
    public void setUp() {
        traceabilityMatrixFile = new File(temporaryFolder, TRACEABILITY_MATRIX_FILE_NAME);
        specificationTree = new SpecificationTree();
        underTest = new TraceabilityMatrixOutput(specificationTree);
    }

    @Test
    public void can_output_the_traceability_matrix() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new TestCase("TC1", "Test Case 1").childOf("SR1"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
        "\\begin{longtable}[l]{@{}lll@{}}",
        "    \\toprule",
        "    Equipment requirement & Software requirement & Test case \\\\* \\midrule",
        "    \\endhead",
        "    ER1 & SR1 & TC1 \\\\* \\bottomrule",
        "\\end{longtable}");
    }

    @Test
    public void can_omit_repeated_equipment_requirements() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new SoftwareRequirement("SR2", "Software Requirement 2").childOf("ER1"));
        specificationTree.attach(new TestCase("TC1", "Test Case 1").childOf("SR1"));
        specificationTree.attach(new TestCase("TC2", "Test Case 2").childOf("SR2"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{longtable}[l]{@{}lll@{}}",
                "    \\toprule",
                "    Equipment requirement & Software requirement & Test case \\\\* \\midrule",
                "    \\endhead",
                "    ER1 & SR1 & TC1 \\\\* \\cmidrule{2-3}",
                "     & SR2 & TC2 \\\\* \\bottomrule",
                "\\end{longtable}");
    }
}