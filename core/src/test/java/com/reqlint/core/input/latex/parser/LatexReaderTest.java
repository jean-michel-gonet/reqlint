package com.reqlint.core.input.latex.parser;

import com.reqlint.core.input.latex.parser.LatexReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.reqlint.core.testutils.TextFileContent.createFileWithLines;
import static com.reqlint.core.testutils.TextFileContent.readLinesFromFile;

class LatexReaderTest {
    private static final String CRLF = "\r\n";
    private static final String CONTENT_1 = "1 Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    private static final String CONTENT_2 = "2 Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
    private static final String CONTENT_3 = "3 Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
    private static final String CONTENT_4 = "4 Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    @TempDir
    private File temporaryFolder;

    private File rootFile;

    @BeforeEach
    public void setUp() {
        rootFile = new File(temporaryFolder, "root.tex");
    }

    @Test
    public void can_follow_includes() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                "\\include{other}",
                CRLF,
                CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);

        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_input() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                "\\input{other}",
                CRLF,
                CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_imports() throws Exception {
        createFileWithLines(new File(temporaryFolder, "/subfolder/one.tex"), List.of(
                CONTENT_1,
                CRLF,
                "\\input{two}"));
        createFileWithLines(new File(temporaryFolder, "/subfolder/two.tex"), List.of(
                CONTENT_2,
                CRLF,
                "\\import{otherfolder/}{three}"));
        createFileWithLines(new File(temporaryFolder, "/otherfolder/three.tex"), List.of(
                CONTENT_3));
        createFileWithLines(rootFile, List.of(
                "\\import{subfolder/}{one}",
                CRLF,
                CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_1, CONTENT_2, CONTENT_3, CONTENT_4);
    }

    @Test
    public void can_follow_nested_imports() throws Exception {
        createFileWithLines(new File(temporaryFolder, "/subfolder/one.tex"), List.of(
                CONTENT_1,
                CRLF,
                "\\import{/subfolder/subsubfolder/}{two}"));
        createFileWithLines(new File(temporaryFolder, "/subfolder/subsubfolder/two.tex"), List.of(
                CONTENT_2,
                CRLF,
                "\\input{three}"));
        createFileWithLines(new File(temporaryFolder, "/subfolder/subsubfolder/three.tex"), List.of(
                CONTENT_3));
        createFileWithLines(rootFile, List.of(
                "\\import{subfolder/}{one}",
                CRLF,
                CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_1, CONTENT_2, CONTENT_3, CONTENT_4);
    }

    @Test
    public void can_follow_sub_imports() throws Exception {
        createFileWithLines(new File(temporaryFolder, "/subfolder/one.tex"), List.of(
                CONTENT_1,
                CRLF,
                "\\subimport{/subsubfolder/}{two}"));
        createFileWithLines(new File(temporaryFolder, "/subfolder/subsubfolder/two.tex"), List.of(
                CONTENT_2,
                CRLF,
                "\\input{three}"));
        createFileWithLines(new File(temporaryFolder, "/subfolder/subsubfolder/three.tex"), List.of(
                CONTENT_3));
        createFileWithLines(rootFile, List.of(
                "\\subimport{subfolder/}{one}",
                CRLF,
                CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_1, CONTENT_2, CONTENT_3, CONTENT_4);
    }

    @Test
    public void can_keep_file_extension_when_specified() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other.xyz"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                "\\input{other.xyz}",
                CRLF,
                CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_manage_when_folders_have_extensions() throws Exception {
        createFileWithLines(new File(temporaryFolder, "/sub.folder/other.tex"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                "\\input{sub.folder/other}",
                CRLF,
                CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_multiple_expansion_commands_in_the_same_line() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other1.tex"), List.of(CONTENT_2));
        createFileWithLines(new File(temporaryFolder, "other2.tex"), List.of(CONTENT_3));
        createFileWithLines(new File(temporaryFolder, "other3.tex"), List.of(CONTENT_4));
        createFileWithLines(rootFile, List.of(
                "\\input{other1}\\include{other2}\\include{other3}",
                CRLF,
                CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(
                CONTENT_2 + CONTENT_3 + CONTENT_4,
                CONTENT_1);
    }

    @Test
    public void can_ignore_expansion_commands_in_comments() throws Exception {
        String commentedInclude = "%\\include{this-comment)";
        createFileWithLines(rootFile, List.of(
                "\\include{other}",
                CRLF,
                commentedInclude,
                CRLF,
                CONTENT_1));
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(
                CONTENT_2));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(
                CONTENT_2,
                commentedInclude,
                CONTENT_1);
    }

    @Test
    public void can_detect_escaped_comments_and_follow_expansion_commands() throws Exception {
        String escapedComment = "\\%";
        createFileWithLines(rootFile, List.of(
                escapedComment + "\\include{other}",
                CRLF,
                CONTENT_1));
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(
                CONTENT_2));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(readLinesFromFile(underTest)).containsExactly(
                escapedComment + CONTENT_2,
                CONTENT_1);
    }

    @Test
    public void can_report_a_missing_file_in_includes() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                CONTENT_1,
                CRLF,
                "\\include{other}",
                CRLF,
                CONTENT_3,
                CRLF,
                "\\include{i-dont-exist}",
                CONTENT_4));

        try (LatexReader underTest = new LatexReader(rootFile)) {
            Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                    .isThrownBy(() -> readLinesFromFile(underTest))
                    .withMessageContaining(rootFile.toString())
                    .withMessageContaining(temporaryFolder.getAbsolutePath())
                    .withMessageContaining("i-dont-exist.tex")
                    .withMessageContaining("line 4");
        }
    }

    @Test
    public void can_report_a_missing_file_in_imports() throws Exception {
        createFileWithLines(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFileWithLines(rootFile, List.of(
                CONTENT_1,
                CRLF,
                "\\import{/}{other}",
                CRLF,
                CONTENT_3,
                CRLF,
                "\\import{missing-folder}{i-dont-exist}",
                CRLF,
                CONTENT_4));

        try (LatexReader underTest = new LatexReader(rootFile)) {
            Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                    .isThrownBy(() -> readLinesFromFile(underTest))
                    .withMessageContaining(rootFile.toString())
                    .withMessageContaining(temporaryFolder.getAbsolutePath())
                    .withMessageContaining("missing-folder")
                    .withMessageContaining("i-dont-exist.tex")
                    .withMessageContaining("line 4");
        }
    }

    @Test
    public void can_to_string_conveniently() throws Exception {
        createFileWithLines(rootFile, List.of(
                CONTENT_1,
                CONTENT_2,
                CONTENT_3,
                CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(underTest.toString()).isEqualTo(rootFile.toString());

        readLinesFromFile(underTest);
        Assertions.assertThat(underTest.toString()).isEqualTo(rootFile.toString() + ": closed");

    }

    @Test
    public void can_keep_track_of_current_file_and_line_number() throws Exception {
        createFileWithLines(rootFile, List.of(
                CONTENT_1,
                CRLF,
                "\\input{other}",
                CRLF,
                CONTENT_4,
                CRLF));

        File other = new File(temporaryFolder, "other.tex");

        createFileWithLines(other, List.of(
                CONTENT_2,
                CRLF,
                CONTENT_3,
                CRLF));

        try (LatexReader underTest = new LatexReader(rootFile);
             BufferedReader bufferedReader = new BufferedReader(underTest)) {

            Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_1);
            Assertions.assertThat(underTest.file()).isEqualTo(rootFile);
            Assertions.assertThat(underTest.lineNumber()).isEqualTo(1);

            Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_2);
            Assertions.assertThat(underTest.file()).isEqualTo(other);
            Assertions.assertThat(underTest.lineNumber()).isEqualTo(1);

            Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_3);
            Assertions.assertThat(underTest.file()).isEqualTo(other);
            Assertions.assertThat(underTest.lineNumber()).isEqualTo(2);

            Assertions.assertThat(bufferedReader.readLine()).isEqualTo("");
            Assertions.assertThat(underTest.file()).isEqualTo(rootFile);
            Assertions.assertThat(underTest.lineNumber()).isEqualTo(2);

            Assertions.assertThat(bufferedReader.readLine()).isEqualTo(CONTENT_4);
            Assertions.assertThat(underTest.file()).isEqualTo(rootFile);
            Assertions.assertThat(underTest.lineNumber()).isEqualTo(3);
        }
    }
}