package com.reqlint.plugin;

import com.reqlint.core.specification.SpecificationTree;
import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.output.TraceabilityMatrixOutput;
import com.reqlint.core.latex.parser.LatexReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Mojo(name = "traceability-matrix", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class TraceabilityMatrixMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(name = "main")
    private String main;

    @Parameter(name = "traceabilityMatrixOutput")
    private String traceabilityMatrixOutput;

    @Parameter(name = "traceabilityWarningsOutput")
    private String traceabilityWarningsOutput;

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
        File inputFile = new File(project.getBuild().getOutputDirectory(), main);
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException(inputFile.getAbsolutePath() + " does not exist or is not a file");
        }

        LatexReader latexReader = new LatexReader(inputFile);
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
        return specificationTreeLoader.load();
    }

    private void writeTraceabilityMatrix(SpecificationTree specificationTree) throws IOException {
        File traceabilityMatrixFile = new File(project.getBuild().getOutputDirectory(), traceabilityMatrixOutput);
        if (traceabilityMatrixFile.exists()) {
            if (!traceabilityMatrixFile.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + traceabilityMatrixFile.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile))) {
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeTraceabilityMatrix(writer);
        }
    }

    private void writeTraceabilityWarnings(SpecificationTree specificationTree) throws  IOException {
        File traceabilityMatrixFile = new File(project.getBuild().getOutputDirectory(), traceabilityWarningsOutput);
        if (traceabilityMatrixFile.exists()) {
            if (!traceabilityMatrixFile.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + traceabilityMatrixFile.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(traceabilityMatrixFile))) {
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeWarnings(writer);
        }
    }
}
