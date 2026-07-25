package com.reqlint.core.latex;

import java.io.IOException;

/**
 * A simple interface to read chars from a token,
 * that is compatible with {@link java.io.Reader}.
 */
@FunctionalInterface
public interface TokenReader {
    int read(char[] cbuf, int off, int len) throws IOException;
}
