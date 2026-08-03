package com.reqlint.plugin;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "compile-latex")
public class CompileLatexMojo extends ReqlintMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompileLatexMojo.class);

    public enum BibTool {
        biber("biber"),
        bibtex("bibtex"),
        none("none");

        private final String executable;

        BibTool(String executable) {
            this.executable = executable;
        }

        public String executable() {
            return this.executable;
        }
    }

    /**
     * Bibliography tool to use: "none", "bibtex", or "biber".
     * Default value is {@code biber}.
     */
    @Parameter(property = "bibTool", defaultValue = "NONE")
    private BibTool bibTool;

    /**
     * Name of the Plan UML executable.
     * Use an absolute file name, or just the name if the executable is defined in the system path.
     * Default value is {@code plantuml}.
     */
    @Parameter(property = "plantUmlTool", defaultValue = "plantuml")
    private String plantUmlTool;

    /**
     * Name of the latex executable in the system.
     * Latex has to be installed prior to run this goal.
     * Default value is {@code lualatex}.
     */
    @Parameter(property = "latexTool", defaultValue = "lualatex")
    private String latexTool;


    /**
     * A list of additional LaTeX documents to compile into PDF.
     * Optional parameter.
     */
    @Parameter(property = "additionalLatexDocuments")
    private List<File> additionalLatexDocuments;


    private File workingDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOGGER.info("Compile LaTeX files");

        List<File> allLatexDocuments = new ArrayList<>();
        allLatexDocuments.add(specificationLatexDocument);
        allLatexDocuments.addAll(additionalLatexDocuments);

        for (File latexDocument : allLatexDocuments) {
            LOGGER.info("Compile LaTeX file {}", latexDocument.getAbsolutePath());
            compileLatexFile(latexDocument);
        }
    }

    private void compileLatexFile(File latexDocument) throws MojoFailureException, MojoExecutionException {

        // Establish the main file and the working directory
        workingDirectory = latexDocument.getParentFile();
        LOGGER.info("Compiling latex document: {}", latexDocument);
        LOGGER.info("Working folder: {}", workingDirectory);

        if (!latexDocument.isFile()) {
            throw new MojoExecutionException("Latex file is not a file, or does not exist");
        }

        // Biber and bibtex behave differently when given the extension,
        // so it is best to remove it.
        String mainFileNameWithExtension = latexDocument.getName();
        String mainFileNameWithoutExtension = FilenameUtils.removeExtension(latexDocument.getName());

        // Step 1: Initial Pass
        runProcess("Pass 1 (LaTeX Initial)", latexTool, "-interaction=nonstopmode", "-halt-on-error", mainFileNameWithExtension);

        // Step 2: Bibliography Tool Execution (if enabled)
        switch (bibTool) {
            case bibtex, biber -> {
                runProcess("Pass 2 (" + bibTool.name() + ")", bibTool.executable(), mainFileNameWithoutExtension);
                runProcess("Pass 3 (LaTeX Post-Bib)", latexTool, "-interaction=nonstopmode",mainFileNameWithExtension);
                runProcess("Pass 4 (LaTeX Final)", latexTool, "-interaction=nonstopmode", mainFileNameWithExtension);
            }
            case none -> {
                runProcess("Pass 2 (LaTeX Final)", latexTool, "-interaction=nonstopmode", mainFileNameWithExtension);
            }
        }
    }

    private void runProcess(String label, String exec, String... args) throws MojoExecutionException, MojoFailureException {
        LOGGER.info("Running [{}] using '{}'...", label, exec);

        List<String> command = new ArrayList<>();
        command.add(exec);
        for (String arg : args) {
            if (arg != null && !arg.isEmpty()) {
                command.add(arg);
            }
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.debug("[{}] {}", exec, line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoFailureException(
                        String.format("Execution failed during %s (exit code %d). Check Maven debug log (-X) or output files in %s",
                                label, exitCode, workingDirectory.getAbsolutePath()));
            }
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing command: " + exec + ". Ensure it is installed and available in PATH.", e);
        }
    }
}