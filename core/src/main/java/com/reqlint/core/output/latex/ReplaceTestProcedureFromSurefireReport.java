package com.reqlint.core.output.latex;

import com.reqlint.core.input.latex.loader.LatexPatterns;
import com.reqlint.core.input.latex.parser.LatexReader;
import com.reqlint.core.model.testreport.TestReport;
import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.utils.AssociatedPattern;
import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReplaceTestProcedureFromSurefireReport {
    private static final String COMMAND = "\\\\testprocedurefromsurefirereport";
    public enum Patterns implements AssociatedPattern {
        OPEN_TEST_CASE(LatexPatterns.OPEN_TEST_CASE),
        CLOSE_TEST_CASE(LatexPatterns.CLOSE_TEST_CASE),
        TEST_PROCEDURE_FROM_SUREFIRE_REPORT(Pattern.compile(COMMAND));

        private final java.util.regex.Pattern pattern;

        Patterns(java.util.regex.Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Matcher match(String line) {
            return pattern.matcher(line);
        }
    }

    private final TestReport testReport;

    public ReplaceTestProcedureFromSurefireReport(TestReport testReport) {
        this.testReport = testReport;
    }

    public void doIt(File rootFile) throws IOException {
        if (!rootFile.isFile()) {
            throw new IOException("File not found or not a file: " + rootFile.getAbsolutePath());
        }

        try (LatexReader latexReader = new LatexReader(rootFile)) {
            List<Replacement> replacements = lookForReplacements(latexReader);
            makeReplacements(replacements);
        }
    }

    private record Replacement(File file, int line, String find, String replace) {}

    private void makeReplacements(List<Replacement> replacements) throws IOException {
        // Group replacements by target file to process each file exactly once
        Map<File, List<Replacement>> replacementsByFile = replacements.stream()
                .collect(Collectors.groupingBy(Replacement::file));

        for (var entry : replacementsByFile.entrySet()) {
            File file = entry.getKey();
            List<Replacement> fileReplacements = entry.getValue();

            // Read all lines into memory
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            // Apply each replacement sequentially to the loaded lines
            for (Replacement rep : fileReplacements) {
                int lineIndex = rep.line() - 1; // Convert 1-based line to 0-based index

                if (lineIndex >= 0 && lineIndex < lines.size()) {
                    String currentLine = lines.get(lineIndex);
                    String updatedLine = currentLine.replace(rep.find(), rep.replace());
                    lines.set(lineIndex, updatedLine);
                }
            }

            // Overwrite the original file
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        }
    }

    private List<Replacement> lookForReplacements(LatexReader latexReader) throws IOException {
        List<Replacement> replacements = new ArrayList<>();
        String currentTestCase = "";

        try (BufferedReader reader = new BufferedReader(latexReader)) {
            String line = "";

            // Consume the rest of the content:
            do {
                while (!line.isEmpty()) {
                    MatchingLiteral<Patterns> matchingLiteral =
                            FindMatchingLiteral.findClosestMatch(Patterns.class, line);
                    if (matchingLiteral == null) {
                        break;
                    }
                    line = line.substring(matchingLiteral.end());
                    switch (matchingLiteral.literal()) {
                        case OPEN_TEST_CASE ->  {
                            Matcher matcher = LatexPatterns.TWO_ARGUMENTS.matcher(line);
                            if (matcher.find()) {
                                currentTestCase = matcher.group(1);
                            }
                            line = line.substring(matcher.end());
                        }
                        case TEST_PROCEDURE_FROM_SUREFIRE_REPORT -> {
                            renderTestRun(currentTestCase).ifPresent(renderedTestRun -> {
                                replacements.add(new Replacement(
                                        latexReader.file(),
                                        latexReader.lineNumber(),
                                        COMMAND, renderedTestRun));
                            });
                        }
                        case CLOSE_TEST_CASE -> {
                            currentTestCase = "";
                        }
                    }
                }
            } while ( (line = reader.readLine()) != null);
        }
        return replacements;
    }

    private Optional<String> renderTestRun(String testCase) throws IOException {
        List<TestRun> testRuns = testReport.reportsOfTestCase(testCase);
        if (testRuns.size() != 1) {
            return Optional.empty();
        }
        TestRun testRun = testRuns.getFirst();
        StringWriter sw = new StringWriter();
        TestResultsOutput.writeTestResult(sw, testRun);
        return Optional.of(sw.toString());
    }
}
