package com.reqlint.core.latex.loader.items;

import com.reqlint.core.latex.loader.exceptions.SpecificationItemNotClosedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import static com.reqlint.core.testutils.TextFileContent.CRLF;
import static com.reqlint.core.testutils.TextFileContent.stringReaderOf;

class TestProcedureTest {
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String EXPECTED_REMAINDER = "EXPECTED_REMAINDER";
    private static final String BLA = "BLA";
    private static final String STAGE_1 = "Stage 1";
    private static final String STAGE_2 = "Stage 2";

    private TestProcedure underTest;

    @BeforeEach
    public void setUp() {
        underTest = new TestProcedure(IDENTIFIER);
    }

    @Test
    public void can_load_stages() throws Exception {
        String line = "    \\stage ";
        BufferedReader reader = new BufferedReader(stringReaderOf(
                STAGE_1,
                CRLF,
                "    \\stage ",
                STAGE_2,
                CRLF,
                "\\end{testprocedure}",
                EXPECTED_REMAINDER));

        underTest.load(line, reader);

        Assertions.assertThat(underTest).isNotNull();
        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TestProcedure.TITLE);
        Assertions.assertThat(underTest.stages()).containsExactly(
                new TestStage(1, STAGE_1),
                new TestStage(2, STAGE_2));
    }

    @Test
    public void can_load_stages_when_all_in_the_same_line() throws Exception {
        String line = "    \\stage ";
        BufferedReader reader = new BufferedReader(stringReaderOf(
                STAGE_1,
                "    \\stage ",
                STAGE_2,
                "\\end{testprocedure}",
                EXPECTED_REMAINDER));

        underTest.load(line, reader);

        Assertions.assertThat(underTest).isNotNull();
        Assertions.assertThat(underTest.identifier()).isEqualTo(IDENTIFIER);
        Assertions.assertThat(underTest.title()).isEqualTo(TestProcedure.TITLE);
        Assertions.assertThat(underTest.stages()).containsExactly(
                new TestStage(1, STAGE_1),
                new TestStage(2, STAGE_2));
    }

    @Test
    public void can_load_stages_over_several_lines() throws Exception {
        String line = "    \\stage ";
        BufferedReader reader = new BufferedReader(stringReaderOf(
                STAGE_1,
                CRLF,
                STAGE_2,
                CRLF,
                "\\end{testprocedure}"));

        underTest.load(line, reader);

        Assertions.assertThat(underTest.stages()).containsExactly(
                new TestStage(1, STAGE_1 + " " + STAGE_2));
    }

    @Test
    public void raises_an_exception_if_there_is_no_closing_tag() {
        String line = "XX";
        BufferedReader reader = new BufferedReader(stringReaderOf(BLA));

        Assertions.assertThatExceptionOfType(SpecificationItemNotClosedException.class)
                .isThrownBy(() -> underTest.load(line, reader));
    }

}