package com.reqlint.core.latex.output;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.TraceabilityMatrixItem;

import java.io.IOException;
import java.io.Writer;

public class TraceabilityMatrixOutput {
    private static final String CRLF = "\r\n";
    private final Writer writer;

    public TraceabilityMatrixOutput(Writer writer) {
        this.writer = writer;
    }

    public void write(SpecificationTree specificationTree) throws IOException {
        writer.write("\\begin{longtable}{lll}");
        writer.write(CRLF);
        writer.write("\\begin{longtable}{lll}");

        for (TraceabilityMatrixItem item : specificationTree.buildTraceabilityMatrix()) {

        }
        writer.write("\\end{longtable}");
    }
}
