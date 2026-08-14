package com.reqlint.plugin;

import com.reqlint.core.latex.loader.SpecificationTreeLoader;
import com.reqlint.core.latex.parser.LatexReader;
import com.reqlint.core.specification.SpecificationTree;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Common ancestor for all reqlint mojos.
 * Contains the common properties.
 */
public abstract class ReqlintMojo extends AbstractMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReqlintMojo.class);

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    /**
     * The latex main files where the software requirements specification is documented.
     * Reqlint follows through {@code \input}, {@code \include}, {@code \import} and {@code \subimport}
     * commands, so you only need to specify the main files.
     */
    @Parameter(name = "specificationLatexDocuments")
    protected List<File> specificationLatexDocuments;

    /**
     * Name of the latex file where to output the tests results.
     */
    @Parameter(name = "testResultsOutput")
    protected File testResultsOutput;

    /**
     * Name of the latex file where to output the warnings collected while composing the test results output.
     */
    @Parameter(name = "testWarningsOutput")
    protected File testWarningsOutput;

    /**
     * Loads the specification from files specified in {@link #specificationLatexDocuments}.
     * @return The specification tree.
     * @throws IOException If one or more files are unreadable.
     */
    protected SpecificationTree readSpecificationTree() throws IOException {
        SpecificationTreeLoader specificationTreeLoader = new SpecificationTreeLoader();
        for (File specificationLatexDocument : specificationLatexDocuments) {
            LOGGER.info("Reading specification Latex Document {}", specificationLatexDocument);
            try (LatexReader reader = new LatexReader(specificationLatexDocument)) {
                specificationTreeLoader.load(reader);
            }
        }
        return specificationTreeLoader.specificationTree();
    }

    protected Writer open(String info, File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IllegalArgumentException("Cannot overwrite " + file.getAbsolutePath());
            }
        }
        LOGGER.info("Writing {} to {}", info, file.getAbsolutePath());

        return new OutputStreamWriter(new FileOutputStream(file));
    }
}
