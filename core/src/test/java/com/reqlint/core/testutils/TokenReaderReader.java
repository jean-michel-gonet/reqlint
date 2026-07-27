package com.reqlint.core.testutils;

import com.reqlint.core.latex.reader.TokenReader;

import java.io.IOException;
import java.io.Reader;

public class TokenReaderReader extends Reader {
    private final TokenReader stringTokenReader;

    public TokenReaderReader(TokenReader stringTokenReader) {
        this.stringTokenReader = stringTokenReader;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return stringTokenReader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        // Nothing to do.
    }
}
