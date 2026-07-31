package com.reqlint.core.latex.output;

import com.reqlint.core.specification.SpecificationTree;
import com.reqlint.core.specification.TraceabilityMatrixItem;
import com.reqlint.core.specification.warnings.SpecificationTreeWarning;

import java.io.IOException;
import java.io.Writer;

public class TraceabilityMatrixOutput {
    private static final String CRLF = "\r\n";
    private final SpecificationTree specificationTree;

    public TraceabilityMatrixOutput(SpecificationTree specificationTree) {
        this.specificationTree = specificationTree;
    }

    public void writeTraceabilityMatrix(Writer writer) throws IOException {
        writer.write("\\begin{longtable}[l]{@{}lll@{}}\r\n");
        writer.write("    \\toprule\r\n");
        writer.write("    Equipment requirement & Software requirement & Test case \\\\* \\midrule\r\n");
        writer.write("    \\endhead\r\n");

        TraceabilityMatrixItem previousItem = null;
        for (TraceabilityMatrixItem item : specificationTree.buildTraceabilityMatrix()) {
            String erId = item.equipmentRequirement().identifier();
            String srId = item.softwareRequirement().identifier();
            String tcId = item.testCase().identifier();
            String midrule = "";

            if (previousItem != null) {
                midrule = "\\midrule";
                if (erId.equals(previousItem.equipmentRequirement().identifier())) {
                    erId = "";
                    midrule = "\\cmidrule{2-3}";
                    if (srId.equals(previousItem.softwareRequirement().identifier())) {
                        srId = "";
                        midrule = "\\cmidrule{3-3}";
                    }
                }
                writer.write(midrule + "\r\n");
            }
            previousItem = item;
            writer.write(String.format("    %s & %s & %s \\\\* ", erId, srId, tcId));
        }
        writer.write("\\bottomrule\r\n");
        writer.write("\\end{longtable}\r\n");
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
