package com.reqlint.core.input.surefire;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lists all test suites in one surefire report folder.
 */
public class SurefireTestSuiteFinder {
    public static final String PREFIX = "TEST";
    public static final String EXTENSION = ".xml";

    private final File surefireFolder;
    private final String prefix;
    private final String extension;

    /**
     * Class constructor.
     * @param surefireFolder A surefire report folder.
     */
    public SurefireTestSuiteFinder(File surefireFolder) {
        this(surefireFolder, PREFIX, EXTENSION);
    }

    /**
     * Class constructor.
     * Unless you have a particular requirement, prefer {@link #SurefireTestSuiteFinder(File)}.
     * @param surefireFolder A surefire report folder.
     * @param prefix A prefix, to identify the XML test suite files.
     * @param extension An extension, to identfy the XML test suite files.
     */
    public SurefireTestSuiteFinder(File surefireFolder, String prefix, String extension) {
        this.surefireFolder = surefireFolder;
        this.prefix = prefix;
        this.extension = extension;
    }

    /**
     * Lists all files in the folder provided to the constructor.
     * @return All files in the folder provided to the constructor.
     * @throws FileNotFoundException If the provided folder does not exist, or is not a folder.
     */
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
