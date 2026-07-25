package com.reqlint.core.latex;

import com.reqlint.core.util.TokenReaderReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

class StringTokenReaderTest {
    private static final String CONTENT_1 = "This is my content";
    private static final String CONTENT_2 = "This is also my content";
    private static final String TWO_LINES_CONTENT = CONTENT_1 + "\n" + CONTENT_2;

    @Test
    public void can_read_one_line() throws Exception {
        StringTokenReader underTest = new StringTokenReader(CONTENT_1);
        TokenReaderReader xx = new TokenReaderReader(underTest);
        BufferedReader bufferedReader = new BufferedReader(xx);
        Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_1);
    }

    @Test
    public void can_read_two_lines() throws Exception {
        StringTokenReader underTest = new StringTokenReader(TWO_LINES_CONTENT);
        TokenReaderReader xx = new TokenReaderReader(underTest);
        BufferedReader bufferedReader = new BufferedReader(xx);
        Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_1);
        Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_2);
    }

}