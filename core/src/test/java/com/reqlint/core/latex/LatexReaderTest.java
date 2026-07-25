package com.reqlint.core.latex;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class LatexReaderTest {
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
        createFile(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFile(rootFile, List.of("\\include{other}", CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);

        Assertions.assertThat(read(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_input() throws Exception {
        createFile(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFile(rootFile, List.of("\\input{other}", CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_imports() throws Exception {
        createFile(new File(temporaryFolder, "/subfolder/one.tex"), List.of(
                CONTENT_1,
                "\\input{two}"));
        createFile(new File(temporaryFolder, "/subfolder/two.tex"), List.of(
                CONTENT_2,
                "\\import{subsubfolder/}{three}"));
        createFile(new File(temporaryFolder, "/subfolder/subsubfolder/three.tex"), List.of(
                CONTENT_3));
        createFile(rootFile, List.of("\\import{subfolder/}{one}", CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(CONTENT_1, CONTENT_2, CONTENT_3, CONTENT_4);
    }

    @Test
    public void can_keep_file_extension_when_specified() throws Exception {
        createFile(new File(temporaryFolder, "other.xyz"), List.of(CONTENT_2));
        createFile(rootFile, List.of("\\input{other.xyz}", CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_manage_when_folders_have_extensions() throws Exception {
        createFile(new File(temporaryFolder, "/sub.folder/other.tex"), List.of(CONTENT_2));
        createFile(rootFile, List.of("\\input{sub.folder/other}", CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(CONTENT_2, CONTENT_1);
    }

    @Test
    public void can_follow_multiple_expansion_commands_in_the_same_line() throws Exception {
        createFile(new File(temporaryFolder, "other1.tex"), List.of(CONTENT_2));
        createFile(new File(temporaryFolder, "other2.tex"), List.of(CONTENT_3));
        createFile(new File(temporaryFolder, "other3.tex"), List.of(CONTENT_4));
        createFile(rootFile, List.of("\\input{other1}\\include{other2}\\include{other3}", CONTENT_1));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(
                CONTENT_2 + CONTENT_3 + CONTENT_4,
                CONTENT_1);
    }

    @Test
    public void can_ignore_expansion_commands_in_comments() throws Exception {
        String commentedInclude = "%\\include{this-comment)";
        createFile(rootFile, List.of(
                "\\include{other}",
                commentedInclude,
                CONTENT_1));
        createFile(new File(temporaryFolder, "other.tex"), List.of(
                CONTENT_2));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(
                CONTENT_2,
                commentedInclude,
                CONTENT_1);
    }

    @Test
    public void can_detect_escaped_comments_and_follow_expansion_commands() throws Exception {
        String escapedComment = "\\%";
        createFile(rootFile, List.of(
                escapedComment + "\\include{other}",
                CONTENT_1));
        createFile(new File(temporaryFolder, "other.tex"), List.of(
                CONTENT_2));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(read(underTest)).containsExactly(
                escapedComment + CONTENT_2,
                CONTENT_1);
    }

    @Test
    public void can_report_a_missing_file_in_includes() throws Exception {
        createFile(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFile(rootFile, List.of(
                CONTENT_1,
                "\\include{other}",
                CONTENT_3,
                "\\include{i-dont-exist}",
                CONTENT_4));

        Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> read(new LatexReader(rootFile)))
                .withMessageContaining(rootFile.toString())
                .withMessageContaining(temporaryFolder + "/i-dont-exist.tex")
                .withMessageContaining("line 4");
    }

    @Test
    public void can_report_a_missing_file_in_imports() throws Exception {
        createFile(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));
        createFile(rootFile, List.of(
                CONTENT_1,
                "\\import{/}{other}",
                CONTENT_3,
                "\\import{missing-folder}{i-dont-exist}",
                CONTENT_4));

        Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> read(new LatexReader(rootFile)))
                .withMessageContaining(rootFile.toString())
                .withMessageContaining(temporaryFolder + "/missing-folder/i-dont-exist.tex")
                .withMessageContaining("line 4");
    }

    @Test
    public void can_to_string_conveniently() throws Exception {
        createFile(rootFile, List.of(
                CONTENT_1,
                CONTENT_2,
                CONTENT_3,
                CONTENT_4));

        LatexReader underTest = new LatexReader(rootFile);
        Assertions.assertThat(underTest.toString()).isEqualTo(rootFile.toString());

        read(underTest);
        Assertions.assertThat(underTest.toString()).isEqualTo(rootFile.toString() + ": closed");

    }

    /**
     * Creates a text file with the specified lines of content.
     * It inserts {@code '\n'} between each line. Not before the first line. Not after the last line.
     *
     * @param file  The file to create. It creates folders if needed.
     * @param lines The lines of content.
     * @throws Exception If something goes wrong.
     */
    private void createFile(File file, List<String> lines) throws Exception {
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        boolean firstLine = true;
        for (String line : lines) {
            if (firstLine) {
                firstLine = false;
            } else {
                bos.write('\r');
                bos.write('\n');
            }
            bos.write(line.getBytes(StandardCharsets.UTF_8));
        }
        bos.close();
    }

    /**
     * Reads all the lines from the specified reader.
     *
     * @param reader A reader, to read from.
     * @return A list with all the lines of the content.
     * @throws Exception Hopefully not.
     */
    private List<String> read(Reader reader) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> readLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            readLines.add(line);
        }
        bufferedReader.close();
        return readLines;
    }
}