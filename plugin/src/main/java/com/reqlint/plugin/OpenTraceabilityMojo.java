package com.reqlint.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import com.reqlint.core.ExecutionContext;
import com.reqlint.core.Step;
import com.reqlint.core.StepFactory;

import java.util.List;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class OpenTraceabilityMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-resources/traceability")
    private String outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean verbose;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // Build execution context with configuration
            ExecutionContext ctx = new ExecutionContext(outputDirectory, verbose, getLog());
            // Instantiate step factory and get ordered steps
            StepFactory factory = new StepFactory();
            List<Step> steps = factory.buildChain(ctx);
            // Execute each step in order
            for (Step step : steps) {
                if (verbose) {
                    getLog().info("Executing step: " + step.getClass().getSimpleName());
                }
                step.execute(ctx);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run Open Traceability goal", e);
        }
    }
}
