package com.reqlint.plugin;

import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.output.latex.ReplaceAutomatedTestDescription;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Mojo(name = "automated-test-description", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class AutomatedTestDescriptionMojo extends ReqlintMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutomatedTestDescriptionMojo.class);

    /**
     * A LaTeX document to replace test descriptions.
     */
    @Parameter(property = "latexDocument", required = true)
    private File latexDocument;

    @Override
    public void execute() throws MojoExecutionException {
        LOGGER.info("test-description goal started");
        try {
            LOGGER.info("Loading surefire test reports");
            TestReport testReport = readSurefireTestReport();
            ReplaceAutomatedTestDescription r = new ReplaceAutomatedTestDescription(testReport);
            r.replaceAutomatedTestDescriptions(latexDocument);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run test-description goal", e);
        }
    }
}
