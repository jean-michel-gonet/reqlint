package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTree;
import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.loader.items.SoftwareRequirement;
import com.reqlint.core.latex.output.TraceabilityMatrixOutput;
import com.reqlint.core.latex.reader.LatexReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Mojo(name = "test-results", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class TestResultsMojo extends AbstractMojo {

    @Parameter(name = "basePath", defaultValue = "${project.build.directory}")
    private String basePath;

    @Parameter(name = "inputFilePath")
    private String inputFilePath;

    @Parameter(name = "surefireReportsFolder")
    private String surefireReportsFolder;

    @Parameter(name = "testResultsFilePath")
    private String traceabilityMatrixFilePath;


    @Override
    public void execute() throws MojoExecutionException {
        try {
            SpecificationTree specificationTree = readSpecificationTree();

            for (SoftwareRequirement softwareRequirement : specificationTree.softwareRequirements()) {

            }
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run traceability-matrix goal", e);
        }
    }

    private SpecificationTree readSpecificationTree() throws IOException {
        File inputFile = new File(new File(basePath), inputFilePath);
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException(inputFile.getAbsolutePath() + " does not exist or is not a file");
        }

        LatexReader latexReader = new LatexReader(inputFile);
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader(latexReader);
        return specificationTreeLoader.load();
    }
}
