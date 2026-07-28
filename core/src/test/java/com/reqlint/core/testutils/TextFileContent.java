package com.reqlint.core.testutils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextFileContent {
    public static String CRLF = "\r\n";

    public static StringReader stringReaderOf(String ... lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return new StringReader(sb.toString());
    }

    /**
     * Creates a text file with the specified lines of content.
     * It inserts {@code '\n'} between each line. Not before the first line. Not after the last line.
     *
     * @param file  The file to create. It creates folders if needed.
     * @param lines The lines of content.
     * @throws Exception If something goes wrong.
     */
    public static void createFileWithLines(File file, List<String> lines) throws Exception {
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        for (String line : lines) {
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
    public static List<String> readLinesFromFile(Reader reader) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> readLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            readLines.add(line);
        }
        bufferedReader.close();
        return readLines;
    }

    /**
     * Use the static methods.
     */
    private TextFileContent() {
        // Nothing to do.
    }
}
