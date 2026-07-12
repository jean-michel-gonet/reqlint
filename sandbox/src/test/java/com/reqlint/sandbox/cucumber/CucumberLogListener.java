package com.reqlint.sandbox.cucumber;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CucumberLogListener implements ConcurrentEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberLogListener.class);

    // Tracks which features have already been logged to prevent duplicate headers in multi-scenario files
    private final Map<URI, Boolean> loggedFeatures = new ConcurrentHashMap<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // Register hooks for the structural lifecycle events
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        TestCase testCase = event.getTestCase();
        URI featureUri = testCase.getUri();

        // 1. Log the Feature Name (Prepended with '#') if it hasn't been logged yet
        loggedFeatures.computeIfAbsent(featureUri, uri -> {
            // Extracts a clean title from the raw file path/name
            String featureName = testCase.getKeyword() + ": " + extractFeatureName(uri);
            LOGGER.info("# {}", featureName);
            return true;
        });

        // 2. Log the Scenario Name (Prepended with '##')
        LOGGER.info("## {}", testCase.getName());
    }

    private void handleTestStepStarted(TestStepStarted event) {
        TestStep testStep = event.getTestStep();

        // We only care about explicit Gherkin steps, ignoring framework @Before/@After hooks
        if (testStep instanceof PickleStepTestStep pickleStep) {
            String stepKeyword = pickleStep.getStep().getKeyword(); // e.g., "Given ", "When "
            String stepText = pickleStep.getStep().getText();       // e.g., "the system has active accounts"

            // 3. Log the Step Definition (Prepended with '###')
            LOGGER.info("### {}{}", stepKeyword, stepText);
        }
    }

    private String extractFeatureName(URI uri) {
        String path = uri.getPath();
        if (path == null) return "Unknown Feature";
        int lastSlash = path.lastIndexOf('/');
        String fileName = lastSlash == -1 ? path : path.substring(lastSlash + 1);
        return fileName.replace(".feature", "");
    }
}