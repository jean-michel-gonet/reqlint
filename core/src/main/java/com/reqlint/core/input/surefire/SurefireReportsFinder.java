package com.reqlint.core.input.surefire;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SurefireReportsFinder {
    public static final String PREFIX = "TEST";
    public static final String EXTENSION = ".xml";

    private final File surefireFolder;
    private final String prefix;
    private final String extension;

    public SurefireReportsFinder(File surefireFolder) throws FileNotFoundException {
        this(surefireFolder, PREFIX, EXTENSION);
    }

    public SurefireReportsFinder(File surefireFolder, String prefix, String extension) throws FileNotFoundException {
        this.surefireFolder = surefireFolder;
        this.prefix = prefix;
        this.extension = extension;
    }

    public List<File> search() throws FileNotFoundException {
        if (!surefireFolder.isDirectory()) {
            throw new FileNotFoundException(surefireFolder + " does not exist or is not a folder.");
        }
        File[] xmlFiles = surefireFolder.listFiles((dir, name) -> name.startsWith(prefix) && name.endsWith(extension));
        if (xmlFiles == null) {
            return Collections.emptyList();
        }
        return Stream.of(xmlFiles).filter(File::isFile).toList();
    }
}
