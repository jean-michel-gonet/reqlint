package com.reqlint.plugin;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "compile-pdf")
public class CompilePdfMojo extends AbstractMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompilePdfMojo.class);

    public enum BibTool {
        BIBER,
        BIBTEX,
        NONE
    }
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The main latex file, that contains links to all other files.
     */
    @Parameter(property = "main", defaultValue = "root.tex", required = true)
    private String main;

    /**
     * The issued PDF file.
     */
    @Parameter(property = "pdfOutput", required = false)
    private String pdfOutput;

    /**
     * Bibliography tool to use: "NONE", "BIBTEX", or "BIBER".
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

    private File workingDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOGGER.info("Build PDF");

        // Establish the main file and the working directory
        File mainFile = new File(project.getBuild().getOutputDirectory(), main);
        workingDirectory = mainFile.getParentFile();
        LOGGER.info("Compiling latex file: {}", mainFile);
        LOGGER.info("Working folder: {}", workingDirectory);

        if (!mainFile.isFile()) {
            throw new MojoExecutionException("Latex file is not a file, or does not exist");
        }

        // Biber and bibtex behave differently when given the extension,
        // so it is best to remove it.
        String mainFileName = mainFile.getName();
        String mainFileNameWithoutExtension = FilenameUtils.removeExtension(mainFile.getName());

        // Step 1: Initial Pass
        runProcess("Pass 1/3 (LaTeX Initial)", latexTool, "-interaction=nonstopmode", "-halt-on-error", mainFileName);

        // Step 2: Bibliography Tool Execution (if enabled)
        switch (bibTool) {
            // BibTeX takes the base file name without extension (e.g., 'root')
            case BIBTEX -> {
                runProcess("Pass 2/4 (BibTeX)", "bibtex", mainFileNameWithoutExtension);
                runProcess("Pass 3/4 (LaTeX Post-Bib)", latexTool, "-interaction=nonstopmode",mainFileName);
                runProcess("Pass 4/4 (LaTeX Final)", latexTool, "-interaction=nonstopmode", mainFileName);
            }
            case BIBER -> {
                // Biber accepts either the base name or full root file
                runProcess("Pass 2/4 (Biber)", "biber", mainFileNameWithoutExtension);
                runProcess("Pass 3/4 (LaTeX Post-Biber)", latexTool, "-interaction=nonstopmode", mainFileName);
                runProcess("Pass 4/4 (LaTeX Final)", latexTool, "-interaction=nonstopmode", mainFileName);
            }
            case NONE -> {
                runProcess("Pass 2/2 (LaTeX Final)", latexTool, "-interaction=nonstopmode", mainFileName);
            }
        }

        // Copy the file to its final destination:
        if (pdfOutput != null && !pdfOutput.isEmpty()) {
            File pdfProducedFile = new File(workingDirectory, mainFileNameWithoutExtension + ".pdf");
            File pdfOutputFile = new File(project.getBuild().getDirectory(), pdfOutput);
            try {
                Files.move(pdfProducedFile.toPath(), pdfOutputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new MojoExecutionException("Error moving PDF file: " + pdfOutputFile.getAbsolutePath(), e);
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