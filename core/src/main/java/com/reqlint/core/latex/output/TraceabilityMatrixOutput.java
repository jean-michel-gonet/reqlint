package com.reqlint.core.latex.output;

import com.reqlint.core.specification.SpecificationItem;
import com.reqlint.core.specification.SpecificationTree;
import com.reqlint.core.specification.items.EquipmentRequirement;
import com.reqlint.core.specification.items.SoftwareRequirement;
import com.reqlint.core.specification.items.TestCase;
import com.reqlint.core.specification.warnings.SpecificationTreeWarning;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TraceabilityMatrixOutput {
    private final SpecificationTree specificationTree;

    public TraceabilityMatrixOutput(SpecificationTree specificationTree) {
        this.specificationTree = specificationTree;
    }

    /**
     * Writes a traceability matrix with all equipment requirements and the linked software requirements.
     * Software requirements not linked to an equipment requirement will not appear.
     * @param writer To write the matrix.
     * @throws IOException Hopefully not.
     */
    public void writeDownstreamSssSrsTrx(Writer writer) throws IOException {
        writeTableHeader(writer, "Upstream", "Downstream");
        for (EquipmentRequirement equipmentRequirement : specificationTree.equipmentRequirements()) {
            if (equipmentRequirement.softwareRequirements().isEmpty()) {
                writeTableRow(writer, equipmentRequirement, null);
            } else {
                for(SoftwareRequirement softwareRequirement: equipmentRequirement.softwareRequirements()) {
                    writeTableRow(writer, equipmentRequirement, softwareRequirement);
                }
            }
        }
        writeTableFooter(writer);
    }

    /**
     * Writes a traceability matrix with all software requirements and the linked equipment requirements.
     * Equipment requirements not linked to a software requirement will not appear.
     * @param writer To write the matrix.
     * @throws IOException Hopefully not.
     */
    public void writeUpstreamSssSrsTrx(Writer writer) throws IOException {
        writeTableHeader(writer, "Downstream", "Upstream");
        for (SoftwareRequirement softwareRequirement : specificationTree.softwareRequirements()) {
            List<EquipmentRequirement> equipmentRequirements = specificationTree.equipmentRequirements(softwareRequirement);
            if (equipmentRequirements.isEmpty()) {
                writeTableRow(writer, softwareRequirement, null);
            } else {
                for (EquipmentRequirement equipmentRequirement : equipmentRequirements) {
                    writeTableRow(writer, softwareRequirement, equipmentRequirement);
                }
            }
        }
        writeTableFooter(writer);
    }

    /**
     * Writes a traceability matrix with all software requirements and the linked test cases.
     * Test cases not linked to a software requirement will not appear.
     * @param writer To write the matrix.
     * @throws IOException Hopefully not.
     */
    public void writeDownstreamSrsTcTrx(Writer writer) throws IOException {
        writeTableHeader(writer, "Upstream", "Downstream");
        for (SoftwareRequirement softwareRequirement : specificationTree.softwareRequirements()) {
            if (softwareRequirement.testCases().isEmpty()) {
                writeTableRow(writer, softwareRequirement, null);
            } else {
                for (TestCase testCase : softwareRequirement.testCases()) {
                    writeTableRow(writer, softwareRequirement, testCase);
                }
            }

        }
        writeTableFooter(writer);
    }

    /**
     * Writes a traceability matrix with all test cases and the linked software.
     * Software requirements not linked to a test case will not appear.
     * @param writer To write the matrix.
     * @throws IOException Hopefully not.
     */
    public void writeUpstreamSrsTcTrx(Writer writer) throws IOException {
        writeTableHeader(writer, "Downstream", "Upstream");
        for (TestCase testCase : specificationTree.testCases()) {
            List<SoftwareRequirement> softwareRequirements = specificationTree.softwareRequirements(testCase);
            if (softwareRequirements.isEmpty()) {
                writeTableRow(writer, testCase, null);
            } else {
                for (SoftwareRequirement softwareRequirement : softwareRequirements) {
                    writeTableRow(writer, testCase, softwareRequirement);
                }
            }
        }
        writeTableFooter(writer);
    }
    private void writeTableHeader(Writer writer, String leftTitle, String rightTitle) throws IOException {
        writer.write("\\begin{xltabular}{\\linewidth}{@{} L c c c c @{\\hspace{15pt}} L c c c c @{}}\r\n");
        writer.write("\\toprule\r\n");
        writer.write("\\multicolumn{5}{c}{\\textbf{" + leftTitle + "}} & \r\n");
        writer.write("\\multicolumn{5}{c}{\\textbf{" + rightTitle + "}} \\\\ \r\n");
        writer.write("\\cmidrule(lr){1-5} \\cmidrule(lr){6-10} \r\n");
        writer.write("\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} & \r\n");
        writer.write("\\textbf{ID and title} & \\textbf{Status} & \\textbf{Derived} & \\textbf{Safety} & \\textbf{Security} \\\\ \r\n");
        writer.write("\\midrule \r\n");
        writer.write("\\endhead \r\n");

        writer.write("\\bottomrule\r\n");
        writer.write("\\endlastfoot\r\n");
    }

    private void writeTableRow(Writer writer, SpecificationItem item1, SpecificationItem item2) throws IOException {
        writeHalfTableRow(writer, item1);
        writer.write(" & ");
        writeHalfTableRow(writer, item2);
        writer.write(" \\\\ \r\n");
    }

    private void writeHalfTableRow(Writer writer, SpecificationItem item) throws IOException {
        if (item == null) {
            writer.write("Not linked & & & & ");
        } else {
            writer.write(String.format("%s - %s", item.identifier(), item.title()));
            writer.write(" & ");
            writer.write(item.status() == null ? "" : item.status());
            writer.write(" & ");
            writer.write(String.format("%b", item.derived()));
            writer.write(" & ");
            writer.write(String.format("%b", item.concernsSafety()));
            writer.write(" & ");
            writer.write(String.format("%b", item.concernsSecurity()));
        }
    }
    private void writeTableFooter(Writer writer) throws IOException {
        writer.write("\\end{xltabular}\r\n");
    }

    public void writeWarnings(Writer writer) throws IOException {
        writer.write("\\begin{itemize}\r\n");
        if (specificationTree.warnings().isEmpty()) {
            writer.write("    \\item No warnings detected.\r\n");
        } else {
            for (SpecificationTreeWarning warning : specificationTree.warnings()) {
                writer.write(String.format("    \\item \\nameref{%s} -- %s\r\n",
                        warning.specificationItem().identifier(),
                        warning.description()));
            }
        }
        writer.write("\\end{itemize}\r\n");
    }
}
