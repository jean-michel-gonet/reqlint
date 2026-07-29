package com.reqlint.core.surefire;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.reqlint.core.testutils.TextFileContent.createFileWithLines;

class TestReportsFinderTest {
    @TempDir
    public File tempDir;

    private TestReportsFinder underTest;

    @BeforeEach
    void setUp() throws Exception {
        underTest = new TestReportsFinder(tempDir);
    }

    @Test
    public void can_list_files() throws Exception {
        File file1 = new File(tempDir, TestReportsFinder.PREFIX + "whatever1" + TestReportsFinder.EXTENSION);
        createFileWithLines(file1, List.of("Hello World!"));
        File file2 = new File(tempDir, TestReportsFinder.PREFIX + "whatever2" + TestReportsFinder.EXTENSION);
        createFileWithLines(file2, List.of("Hello World!"));
        File file3 = new File(tempDir, TestReportsFinder.PREFIX + "whatever3" + TestReportsFinder.EXTENSION);
        createFileWithLines(file3, List.of("Hello World!"));

        File file4 = new File(tempDir, "whatever1" + TestReportsFinder.EXTENSION);
        createFileWithLines(file4, List.of("Hello World!"));
        File file5 = new File(tempDir, TestReportsFinder.PREFIX + "whatever2.txt");
        createFileWithLines(file5, List.of("Hello World!"));

        Assertions.assertThat(underTest.search()).containsExactlyInAnyOrder(file1, file2, file3);
    }

    @Test
    public void can_list_files_when_there_are_none() throws Exception {
        Assertions.assertThat(underTest.search()).isEmpty();
    }

    @Test
    public void raises_an_exception_when_surefire_folder_does_not_exist() throws Exception {
        Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> new TestReportsFinder(new File(tempDir, "i-dont-exist")).search())
                .withMessageContaining("i-dont-exist");
    }
    @Test
    public void raises_an_exception_when_surefire_folder_is_a_file() throws Exception {
        File file = new File(tempDir, "i-am-a-file.txt");
        createFileWithLines(file, List.of("Hello World!"));
        Assertions.assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> new TestReportsFinder(file).search())
                .withMessageContaining("i-am-a-file.txt");
    }
}