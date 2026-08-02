package com.reqlint.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

/**
 * Common ancestor for all reqlint mojos.
 * Contains the common properties.
 */
public abstract class ReqlintMojo extends AbstractMojo {

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    protected List<MavenProject> allProjects;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * The latex file where the software requirements specification is documented.
     * Reqlint can follow through {@code \input}, {@code \include}, {@code \import} and {@code \subimport}
     * commands.
     */
    @Parameter(name = "specificationLatexDocument")
    protected String specificationLatexDocument;

    /**
     * Name of the latex file where to output the traceability matrix.
     */
    @Parameter(name = "traceabilityMatrixOutput")
    protected String traceabilityMatrixOutput;

    /**
     * Name of the latex file where to output the warnings detected while building the traceability matrix.
     */
    @Parameter(name = "traceabilityWarningsOutput")
    protected String traceabilityWarningsOutput;

    /**
     * Name of the latex file where to output the tests results.
     */
    @Parameter(name = "testResultsOutput")
    protected String testResultsOutput;

    /**
     * Name of the latex file where to output the warnings collected while composing the test results output.
     */
    @Parameter(name = "testWarningsOutput")
    protected String testWarningsOutput;
}
