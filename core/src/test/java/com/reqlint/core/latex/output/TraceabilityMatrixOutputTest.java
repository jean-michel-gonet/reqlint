package com.reqlint.core.latex.output;

import com.reqlint.core.specification.SpecificationTree;
import com.reqlint.core.specification.items.EquipmentRequirement;
import com.reqlint.core.specification.items.SoftwareRequirement;
import com.reqlint.core.specification.items.TestCase;
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
    private static final String TRACEABILITY_WARNINGS_FILE_NAME = "traceability-warnings.tex";
    @TempDir
    public File temporaryFolder;

    private TraceabilityMatrixOutput underTest;
    private File traceabilityMatrixFile;
    private File traceabilityWarningsFile;
    private SpecificationTree specificationTree;

    @BeforeEach
    public void setUp() {
        traceabilityMatrixFile = new File(temporaryFolder, TRACEABILITY_MATRIX_FILE_NAME);
        traceabilityWarningsFile = new File(temporaryFolder, TRACEABILITY_WARNINGS_FILE_NAME);
        specificationTree = new SpecificationTree();
        underTest = new TraceabilityMatrixOutput(specificationTree);
    }

    @Test
    public void can_output_upstream_trx() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new EquipmentRequirement("ER2", "Equipment Requirement 2"));
        specificationTree.attach(new SoftwareRequirement("SR2", "Software Requirement 2").childOf("ER2"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeUpstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "SR1 - Software Requirement 1 &  & false & false & false & ER1 - Equipment Requirement 1 &  & false & false & false \\\\ ",
                "SR2 - Software Requirement 2 &  & false & false & false & ER2 - Equipment Requirement 2 &  & false & false & false \\\\ ",
                "\\end{xltabular}"
        );
    }

    @Test
    public void can_output_upstream_trx_repeating_equipment_requirements() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new SoftwareRequirement("SR2", "Software Requirement 2").childOf("ER1"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeUpstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "SR1 - Software Requirement 1 &  & false & false & false & ER1 - Equipment Requirement 1 &  & false & false & false \\\\ ",
                "SR2 - Software Requirement 2 &  & false & false & false & ER1 - Equipment Requirement 1 &  & false & false & false \\\\ ",
                "\\end{xltabular}"
        );
    }

    @Test
    public void can_output_upstream_trx_with_unlinked_software_requirements() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new SoftwareRequirement("SR2", "Software Requirement 2").childOf("ER2"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeUpstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "SR1 - Software Requirement 1 &  & false & false & false & ER1 - Equipment Requirement 1 &  & false & false & false \\\\ ",
                "SR2 - Software Requirement 2 &  & false & false & false & Not linked & & & &  \\\\ ",
                "\\end{xltabular}"
        );
    }

    @Test
    public void can_output_downstream_trx() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new EquipmentRequirement("ER2", "Equipment Requirement 2"));
        specificationTree.attach(new SoftwareRequirement("SR2", "Software Requirement 2").childOf("ER2"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeDownstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "ER1 - Equipment Requirement 1 &  & false & false & false & SR1 - Software Requirement 1 &  & false & false & false \\\\ ",
                "ER2 - Equipment Requirement 2 &  & false & false & false & SR2 - Software Requirement 2 &  & false & false & false \\\\ ",
                "\\end{xltabular}"
        );
    }

    @Test
    public void can_output_downstream_trx_repeating_equipment_requirements() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new EquipmentRequirement("ER2", "Equipment Requirement 2"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1").childOf("ER2"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeDownstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "ER1 - Equipment Requirement 1 &  & false & false & false & SR1 - Software Requirement 1 &  & false & false & false \\\\ ",
                "ER2 - Equipment Requirement 2 &  & false & false & false & SR1 - Software Requirement 1 &  & false & false & false \\\\ ",
                "\\end{xltabular}"
        );
    }

    @Test
    public void can_output_downstream_trx_with_unlinked_equipment_requirements() throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new EquipmentRequirement("ER2", "Equipment Requirement 2"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile));
        underTest.writeDownstreamTraceabilityMatrix(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityMatrixFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}",
                "\\toprule",
                "\\multicolumn{5}{c}{\\textbf{Upstream}} & ",
                "\\multicolumn{5}{c}{\\textbf{Downstream}} \\\\ ",
                "\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & ",
                "\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ ",
                "\\midrule ",
                "\\endhead ",
                "\\bottomrule",
                "\\endlastfoot",
                "ER1 - Equipment Requirement 1 &  & false & false & false & SR1 - Software Requirement 1 &  & false & false & false \\\\ ",
                "ER2 - Equipment Requirement 2 &  & false & false & false & Not linked & & & &  \\\\ ",
                "\\end{xltabular}"
        );
    }


    @Test
    public void can_output_warnings_when_there_are_none()  throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.attach(new TestCase("TC1", "Test Case 1").childOf("SR1"));
        specificationTree.attach(new TestCase("TC2", "Test Case 2").childOf("SR1"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityWarningsFile));
        underTest.writeWarnings(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityWarningsFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{itemize}",
                "    \\item No warnings detected.",
                "\\end{itemize}");
    }

    @Test
    public void can_output_warnings_when_there_are_some()  throws Exception {
        specificationTree.attach(new EquipmentRequirement("ER1", "Equipment Requirement 1"));
        specificationTree.attach(new SoftwareRequirement("SR1", "Software Requirement 1").childOf("ER1"));
        specificationTree.verify();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityWarningsFile));
        underTest.writeWarnings(writer);
        writer.close();

        List<String> lines = TextFileContent.readLinesFromFile(new FileReader(traceabilityWarningsFile));

        Assertions.assertThat(lines).containsExactly(
                "\\begin{itemize}",
                "    \\item \\nameref{SR1} -- Software requirement SR1 has no linked test cases",
                "\\end{itemize}");
    }

}