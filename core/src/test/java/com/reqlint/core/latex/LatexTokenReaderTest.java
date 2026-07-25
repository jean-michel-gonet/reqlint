package com.reqlint.core.latex;

import com.reqlint.core.util.TokenReaderReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class LatexTokenReaderTest {
    private static final String CONTENT_1 = "This is the content";
    private static final String CONTENT_2 = "This is the other content";

    @TempDir
    private File temporaryFolder;

    private File rootFile;

    @BeforeEach
    public void setUp() {
        rootFile = new File(temporaryFolder, "root.tex");
    }
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

    @Test
    public void can_follow_includes() throws Exception {
        List<String> expectedContent = new ArrayList<>();
        List<String> includes = new ArrayList<>();
        for (int n = 0; n < 20; n++) {
            includes.add("\\include{other-" + n + "}");
            String content = "This is the content of file number " + n;
            createFile(new File(temporaryFolder, "other-" + n + ".tex"), List.of(content));
            expectedContent.add(content);
        }
        createFile(rootFile, includes);

        LatexTokenReader underTest = new LatexTokenReader(rootFile);
        BufferedReader bufferedReader = new BufferedReader(underTest);
        List<String> actualContent = new ArrayList<>();
        String content;
        while( (content = bufferedReader.readLine()) != null) {
            actualContent.add(content);
        }
        Assertions.assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    public void can_do_something_nice() throws Exception {
        createFile(rootFile, List.of("\\include{other}", CONTENT_1));
        createFile(new File(temporaryFolder, "other.tex"), List.of(CONTENT_2));

        LatexTokenReader underTest = new LatexTokenReader(rootFile);
        BufferedReader reader = new BufferedReader(new TokenReaderReader(underTest));

        List<String> readLines = new ArrayList<>();
        String line;
        while( (line = reader.readLine()) != null) {
            readLines.add(line);
        }
        reader.close();

        Assertions.assertThat(readLines).containsExactly(CONTENT_2, CONTENT_1);
    }

}