package com.reqlint.core.input.latex.parser;

import com.reqlint.core.utils.FindMatchingLiteral;
import com.reqlint.core.utils.MatchingLiteral;

import java.io.*;
import java.util.*;

/**
 * Reads a LaTeX file, replacing all
 * {@code \include{path-to-file}}, {@code \import{path-to-file}} and {@code \input{path-to-file}}
 * by their actual content.
 */
public class LatexReader extends Reader implements TokenReader {
    private final File rootDirectory;
    private final File searchPath;
    private final File inputFile;
    private Reader reader;
    private TokenReader currentToken;
    private final Queue<TokenReader> tokenReaders = new ArrayDeque<>();
    private boolean isClosed = false;
    private final char[] buffer = new char[8096];
    private int bufferLength = 0;
    private int bufferPosition = bufferLength;
    private int lineNumber = 0;

    /**
     * Class constructor.
     * @param inputFile A root LaTeX document.
     * @throws FileNotFoundException If the specified file does not exist.
     */
    public LatexReader(File inputFile) throws FileNotFoundException {
        this(inputFile.getParentFile(), inputFile.getParentFile(), inputFile);
    }

    /**
     * Class constructor.
     * @param rootDirectory The folder where the latex compilation was launched.
     * @param searchPath Internal latex search path, used by {@code \input} and {@code \include} commands.
     * @param inputFile The file to read.
     * @throws FileNotFoundException If the root LaTeX document does not exist.
     */
    protected LatexReader(File rootDirectory, File searchPath, File inputFile) throws FileNotFoundException {
        if (!inputFile.isFile()) {
            throw new FileNotFoundException(inputFile + " is not a file or does not exist.");
        }
        this.rootDirectory = rootDirectory;
        this.searchPath = searchPath;
        this.inputFile = inputFile;

    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (isClosed) {
            return -1;
        }

        // Look for chars in the current token:
        if (currentToken != null) {
            int n = currentToken.read(cbuf, off, len);
            if (n >= 0) {
                return n;
            }
            currentToken = null;
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

    @Override
    public File file() {
        return currentToken == null ? this.inputFile : currentToken.file();
    }

    @Override
    public int lineNumber() {
        return currentToken == null ? lineNumber : currentToken.lineNumber();
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
            // Fetch more chars if we've read the whole buffer (or we never loaded the buffer):
            if (bufferPosition >= bufferLength) {
                // If not yet done, open the file:
                if (reader == null) {
                    // We've checked the existence of the file in the constructor:
                    reader = new FileReader(inputFile);
                }
                // Fill in the buffer:
                bufferLength = reader.read(buffer, 0, buffer.length);

                // This is the EOF:
                if (bufferLength < 0) {
                    break;
                }

                // Reset the reading position to the beginning of the buffer:
                bufferPosition = 0;
            }
            c = buffer[bufferPosition++];
            if (c == '\r') {
                continue;
            }
            stringBuilder.append(c);
        } while (c != '\n');

        // Increment the current line number:
        lineNumber++;

        // We've found nothing, this is the EOF:
        if (stringBuilder.isEmpty()) {
            return null;
        }

        // We've found content:
        return stringBuilder.toString();
    }

    private Collection<? extends TokenReader> extractTokenReaders(String line) throws FileNotFoundException {
        List<TokenReader> extractedTokenReaders = new ArrayList<>();
        do {
            MatchingLiteral<ExpansionCommand> matchingLiteral =
                    FindMatchingLiteral.findClosestMatch(ExpansionCommand.class, line);

            // If there are no more expansion commands in the line, then the token is the whole line:
            if (matchingLiteral == null) {
                extractedTokenReaders.add(new StringTokenReader(
                        inputFile,
                        lineNumber,
                        line));
                break;
            }

            // The first token starts at position 0, and ends at the beginning of the first expansion command:
            if (matchingLiteral.start() > 0) {
                extractedTokenReaders.add(new StringTokenReader(
                        inputFile,
                        lineNumber,
                        line.substring(0, matchingLiteral.start())));
            }

            // The second token is the expansion command:
            extractedTokenReaders.add(createExpansionCommand(matchingLiteral));

            // Look after the expansion command:
            line = line.substring(matchingLiteral.end());
        } while (!line.isEmpty());

        return extractedTokenReaders;
    }

    private TokenReader createExpansionCommand(MatchingLiteral<ExpansionCommand> match) throws FileNotFoundException {
        return switch (match.literal()) {
            case INPUT, INCLUDE -> createInputCommand(match.group(3));
            case IMPORT -> createImportCommand(match.group(3), match.group(4));
            case SUBIMPORT -> createSubImportCommand(match.group(3), match.group(4));
            case COMMENT -> createComment(match.group(3));
        };
    }

    private TokenReader createInputCommand(String filePath) throws FileNotFoundException {
        String filePathWithExtension = appendTexExtension(filePath);
        File newFilePath = new File(searchPath, filePathWithExtension);
        try {
            return new LatexReader(rootDirectory, searchPath, newFilePath);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(inputFile + ", line " + lineNumber + ": " + e.getMessage());
        }
    }

    private TokenReader createImportCommand(String searchPath, String inputFile) throws FileNotFoundException {
        File newSearchPath = new File(this.rootDirectory, searchPath);
        String inputFileWithExtension = appendTexExtension(inputFile);
        File newInputFile = new File(newSearchPath, inputFileWithExtension);
        try {
            return new LatexReader(rootDirectory, newSearchPath, newInputFile);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(this.inputFile + ", line " + lineNumber + ": " + e.getMessage());
        }
    }

    private TokenReader createSubImportCommand(String searchPath, String inputFile) throws FileNotFoundException {
        File newSearchPath = new File(this.rootDirectory, searchPath);
        String inputFileWithExtension = appendTexExtension(inputFile);
        File newInputFile = new File(newSearchPath, inputFileWithExtension);
        try {
            return new LatexReader(newSearchPath, newSearchPath, newInputFile);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(this.inputFile + ", line " + lineNumber + ": " + e.getMessage());
        }
    }

    private TokenReader createComment(String comment) {
        return new StringTokenReader(file(), lineNumber(), "%" + comment);
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
        reader.close();
        isClosed = true;
    }

    @Override
    public String toString() {
        return inputFile + (isClosed ? ": closed" : "");
    }
}
