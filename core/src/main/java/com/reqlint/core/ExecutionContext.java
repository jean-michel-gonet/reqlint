package com.reqlint.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

public class ExecutionContext {
    private final Path outputDirectory;
    private final boolean verbose;
    private final Log log;
    private final Map<String, Object> data = new HashMap<>();

    public ExecutionContext(String outputDir, boolean verbose, Log log) {
        this.outputDirectory = Paths.get(outputDir).toAbsolutePath();
        this.verbose = verbose;
        this.log = log;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Log getLog() {
        return log;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public <T> T get(String key) {
        @SuppressWarnings("unchecked")
        T value = (T) data.get(key);
        return value;
    }
}
