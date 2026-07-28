package com.reqlint.core.latex.output;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.TraceabilityMatrixItem;
import com.reqlint.core.latex.loader.warnings.SpecificationTreeWarning;

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

        for (TraceabilityMatrixItem item : specificationTree.buildTraceabilityMatrix()) {
            writer.write(String.format("    \\nameref{%s} & \\nameref{%s} & \\nameref{%s} \\\\* \\midrule\r\n",
                    item.equipmentRequirement().identifier(),
                    item.softwareRequirement().identifier(),
                    item.testCase().identifier()));
        }
        writer.write("    \\bottomrule\r\n");
        writer.write("\\end{longtable}\r\n");
    }

    public void writeWarnings(Writer writer) throws IOException {
        writer.write("\\begin{itemize}\r\n");
        for (SpecificationTreeWarning warning : specificationTree.warnings()) {
            writer.write(String.format("    \\item \\nameref{%s} -- %s\r\n",
                    warning.specificationItem().identifier(),
                    warning.description()));
        }
        writer.write("\\end{itemize}\r\n");
    }
}
