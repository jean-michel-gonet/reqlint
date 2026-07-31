package com.reqlint.core.latex.parser;

import java.io.File;
import java.io.IOException;

/**
 * A simple interface to read chars from a token,
 * that is compatible with {@link java.io.Reader}.
 */
public interface TokenReader {
    int read(char[] cbuf, int off, int len) throws IOException;
    File file();
    int lineNumber();
}
