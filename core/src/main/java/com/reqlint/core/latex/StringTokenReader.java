package com.reqlint.core.latex;

/**
 * A token reader based on a simple {@link String}.
 */
public class StringTokenReader implements TokenReader {
    private final String tokenContent;
    int readingPosition = 0;

    /**
     * Class constructor.
     * @param tokenContent The content.
     */
    public StringTokenReader(String tokenContent) {
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
    public String toString() {
        return tokenContent;
    }
}
