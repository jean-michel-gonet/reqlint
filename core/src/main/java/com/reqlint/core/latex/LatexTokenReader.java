package com.reqlint.core.latex;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads a LaTeX file, replacing all
 * {@code \include{path-to-file}}, {@code \import{path-to-file}} and {@code \input{path-to-file}}
 * by their actual content.
 */
public class LatexTokenReader extends Reader implements TokenReader {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("\\\\(import)\\{([^}]+)}\\{([^}]+)}");
    private static final Pattern INPUT_PATTERN = Pattern.compile("\\\\(input)\\{([^}]+)}");
    private static final Pattern INCLUDE_PATTERN = Pattern.compile("\\\\(include)\\{([^}]+)}");
    private static final Pattern[] EXPANSION_COMMAND_PATTERNS = {IMPORT_PATTERN, INPUT_PATTERN, INCLUDE_PATTERN};

    private final File inputFile;
    private final File basePath;
    private Reader reader;
    private TokenReader currentToken;
    private final Queue<TokenReader> tokenReaders = new ArrayDeque<>();
    private boolean isClosed = false;
    private final char[] buffer = new char[8096];
    private int bufferLength = 0;
    private int bufferPosition = bufferLength;

    /**
     * Class constructor.
     * @param inputFile A root LaTeX document.
     * @throws FileNotFoundException If the specified file does not exist.
     */
    public LatexTokenReader(File inputFile) throws FileNotFoundException {
        this(inputFile.getParentFile(), inputFile);
    }

    /**
     * Class constructor.
     * @param basePath The base folder, to interpret path found in the LaTeX document.
     * @param inputFile The root LaTeX document.
     * @throws FileNotFoundException If the root document does not exist.
     */
    public LatexTokenReader(File basePath, File inputFile) throws FileNotFoundException {
        this.basePath = basePath;
        this.inputFile = inputFile;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (isClosed) {
            return -1;
        }

        int n = -1;
        // Look for chars in the current token:
        if (currentToken != null) {
            n = currentToken.read(cbuf, off, len);
            if (n >= 0) {
                return n;
            }
        }

        // Look for chars in the next token:
        if (!tokenReaders.isEmpty()) {
            currentToken = tokenReaders.poll();
            return read(cbuf, off, len);
        }

        // Fetch more tokens:
        fetchMoreTokens();
        if (tokenReaders.isEmpty()) {
            close();
            return -1;
        }

        // Try again:
        return read(cbuf, off, len);
    }

    private void fetchMoreTokens() throws IOException {
        String line = readLine();

        // If there are no more lines, we're finished:
        if (line == null) {
            return;
        }

        // Breaks the line into tokens:
        tokenReaders.addAll(extractTokenReaders(line));
    }

    private String readLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        char c;
        do {
            // Fetch more chars:
            if (bufferPosition >= bufferLength) {
                if (reader == null) {
                    reader = new FileReader(inputFile);
                }
                bufferLength = reader.read(buffer, 0, buffer.length);
                if (bufferLength < 0) {
                    break;
                }
                bufferPosition = 0;
            }
            c = buffer[bufferPosition++];
            if (c == '\r') {
                continue;
            }
            stringBuilder.append(c);
        } while (c != '\n');

        if (stringBuilder.isEmpty()) {
            return null;
        }
        return stringBuilder.toString();
    }

    private Collection<? extends TokenReader> extractTokenReaders(String line) {
        List<TokenReader> extractedTokenReaders = new ArrayList<>();
        do {
            Matcher closestMatcher = closestExpansionCommand(line);

            // If there are no more expansion commands in the line, then the token is the whole line:
            if (closestMatcher == null) {
                extractedTokenReaders.add(new StringTokenReader(line));
                break;
            }

            // The first token starts at position 0, and ends at the beginning of the first expansion command:
            if (closestMatcher.start() > 0) {
                extractedTokenReaders.add(new StringTokenReader(line.substring(0, closestMatcher.start() - 1)));
            }

            // The second token is the expansion command:
            extractedTokenReaders.add(createExpansionCommand(closestMatcher));

            // Look after the expansion command:
            line = line.substring(closestMatcher.end());
        } while (!line.isEmpty());

        return extractedTokenReaders;
    }

    private Matcher closestExpansionCommand(String line) {
        int closestStart = line.length();
        Matcher closestMatcher = null;
        for (Pattern pattern : EXPANSION_COMMAND_PATTERNS) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && matcher.start() < closestStart) {
                closestStart = matcher.start();
                closestMatcher = matcher;
            }
        }
        return closestMatcher;
    }

    private TokenReader createExpansionCommand(Matcher matcher) {
        String command = matcher.group(1);
        return switch (command) {
            case "input", "include" -> createInputCommand(matcher.group(2));
            case "import" -> createImportCommand(matcher.group(2), matcher.group(3));
            default -> throw new RuntimeException("Unknown expansion command: " + command);
        };
    }

    private TokenReader createInputCommand(String filePath) {
        String filePathWithExtension = appendTexExtension(filePath);
        File newFilePath = new File(basePath, filePathWithExtension);
        try {
            return new LatexTokenReader(basePath, newFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + newFilePath + " does not exist, or is not a file");
        }
    }

    private TokenReader createImportCommand(String basePath, String filePath) {
        File newBasePath = new File(this.basePath, basePath);
        String filePathWithExtension = appendTexExtension(filePath);
        File newFilePath = new File(newBasePath, filePathWithExtension);
        try {
            return new LatexTokenReader(newBasePath, newFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + newFilePath + " does not exist, or is not a file");
        }
    }

    private String appendTexExtension(String filePath) {
        int n = filePath.lastIndexOf(".");
        if (n < 0) {
            return filePath + ".tex";
        }
        String extension = filePath.substring(n);
        n = extension.indexOf("/");
        if (n > 0) {
            return filePath + ".tex";
        }
        return filePath;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        isClosed = true;
    }

    @Override
    public String toString() {
        return inputFile + (isClosed ? ": closed" : "");
    }
}
