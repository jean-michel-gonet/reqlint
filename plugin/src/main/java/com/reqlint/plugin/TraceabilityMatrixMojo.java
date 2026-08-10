package com.reqlint.plugin;

import com.reqlint.core.latex.output.TraceabilityMatrixOutput;
import com.reqlint.core.specification.SpecificationTree;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Mojo(name = "traceability-matrix", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class TraceabilityMatrixMojo extends ReqlintMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            SpecificationTree specificationTree = readSpecificationTree();
            specificationTree.verify();
            writeTraceabilityMatrix(specificationTree);
            writeTraceabilityWarnings(specificationTree);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run traceability-matrix goal", e);
        }
    }

    private void writeTraceabilityMatrix(SpecificationTree specificationTree) throws IOException {
        TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);

        if (upstreamTraceabilityMatrixOutput != null) {
            if (upstreamTraceabilityMatrixOutput.exists()) {
                if (!upstreamTraceabilityMatrixOutput.delete()) {
                    throw new IllegalArgumentException("Cannot overwrite " + upstreamTraceabilityMatrixOutput.getAbsolutePath());
                }
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(upstreamTraceabilityMatrixOutput))) {
                traceabilityMatrixOutput.writeUpstreamTraceabilityMatrix(writer);
            }
        }

        if (downstreamTraceabilityMatrixOutput != null) {
            if (downstreamTraceabilityMatrixOutput.exists()) {
                if (!downstreamTraceabilityMatrixOutput.delete()) {
                    throw new IllegalArgumentException("Cannot overwrite " + downstreamTraceabilityMatrixOutput.getAbsolutePath());
                }
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(downstreamTraceabilityMatrixOutput))) {
                traceabilityMatrixOutput.writeDownstreamTraceabilityMatrix(writer);
            }
        }
    }

    private void writeTraceabilityWarnings(SpecificationTree specificationTree) throws  IOException {
        if (traceabilityWarningsOutput.exists()) {
            if (!traceabilityWarningsOutput.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + traceabilityWarningsOutput.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityWarningsOutput))) {
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeWarnings(writer);
        }
    }
}
