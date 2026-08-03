package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.output.TraceabilityMatrixOutput;
import com.reqlint.core.latex.parser.LatexReader;
import com.reqlint.core.specification.SpecificationTree;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Mojo(name = "traceability-matrix", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
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

    private SpecificationTree readSpecificationTree() throws IOException {
        if (!specificationLatexDocument.isFile()) {
            throw new IllegalArgumentException(specificationLatexDocument.getAbsolutePath() + " does not exist or is not a file");
        }

        LatexReader latexReader = new LatexReader(specificationLatexDocument);
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
        return specificationTreeLoader.load();
    }

    private void writeTraceabilityMatrix(SpecificationTree specificationTree) throws IOException {
        if (traceabilityMatrixOutput.exists()) {
            if (!traceabilityMatrixOutput.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + traceabilityMatrixOutput.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixOutput))) {
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeTraceabilityMatrix(writer);
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
