package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.output.TraceabilityMatrixOutput;
import com.reqlint.core.latex.reader.LatexReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

@Mojo(name = "traceability-matrix", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class TraceabilityMatrixMojo extends AbstractMojo {

    @Parameter(name = "basePath", defaultValue = "${project.build.directory}")
    private String basePath;

    @Parameter(name = "inputFilePath")
    private String inputFilePath;

    @Parameter(name = "outputFilePath")
    private String outputFilePath;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            File baseFolder = new File(basePath);
            File inputFile = new File(baseFolder, inputFilePath);
            if (!inputFile.isFile()) {
                throw new IllegalArgumentException(inputFile.getAbsolutePath() + " does not exist or is not a file");
            }

            LatexReader latexReader = new LatexReader(inputFile);
            SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
            SpecificationTree specificationTree = specificationTreeLoader.load();
            specificationTree.verify();

            File outputFile = new File(baseFolder, outputFilePath);
            if (outputFile.exists()) {
                if (!outputFile.delete()) {
                    throw new IllegalArgumentException("Cannot overwrite " + outputFile.getAbsolutePath());
                }
            }

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile));
            TraceabilityMatrixOutput traceabilityMatrixOutput = new TraceabilityMatrixOutput(specificationTree);
            traceabilityMatrixOutput.writeTraceabilityMatrix(writer);
            traceabilityMatrixOutput.writeWarnings(writer);
            writer.close();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run traceability-matrix goal", e);
        }
    }
}
