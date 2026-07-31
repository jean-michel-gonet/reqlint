package com.reqlint.core.latex.parser;

import java.io.File;

/**
 * A token reader based on a simple {@link String}.
 */
public class StringTokenReader implements TokenReader {
    private final String tokenContent;
    private final File file;
    private final int lineNumber;

    int readingPosition = 0;

    /**
     * Class constructor.
     * @param tokenContent The content.
     */
    public StringTokenReader(File file, int lineNumber, String tokenContent) {
        this.file = file;
        this.lineNumber = lineNumber;
        this.tokenContent = tokenContent;
    }

    @Override
    public int read(char[] cbuf, int off, int len) {
        if (readingPosition == tokenContent.length()) {
            return -1;
        }
        int n;
        for(n = 0; n < len; n++) {
            if (readingPosition >= tokenContent.length()) {
                break;
            }
            char c = tokenContent.charAt(readingPosition++);
            cbuf[n + off] = c;
        }
        return n;
    }

    @Override
    public File file() {
        return file;
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return tokenContent;
    }
}
