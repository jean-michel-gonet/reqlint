package com.reqlint.cucumber;

import com.reqlint.core.surefire.report.LogPatternsAndFormats;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

/**
 * Adds specific logs before starting the scenario, and before each cucumber step.
 * This class is compatible with the
 */
public class ReqlintSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReqlintSteps.class);

    private int cucumberStepNumber = 0;

    @Before
    public void before(Scenario scenario) {
        cucumberStepNumber = 0;
        String testIdentifier = extractTestIdentifier(scenario.getUri());
        LOGGER.info(LogPatternsAndFormats.preparationOf(testIdentifier, scenario.getName()));
    }

    private String extractTestIdentifier(URI uri) {
        String[] segments = uri.toString().split("/");
        return segments[segments.length - 1];
    }

    @BeforeStep
    public void logStepName(Scenario scenario) throws Exception {
        StepDescription stepDescription = extractStepDescription(scenario);
        LOGGER.info(LogPatternsAndFormats.stepOf(stepDescription.number(), stepDescription.keyword(), stepDescription.text()));
    }

    @Given("Stage {int}")
    public void start_stage(int stepNumber) {
        // Nothing to do.
    }

    @Given("Stage {int} - {string}")
    public void start_stage(int stepNumber, String stepName) {
        // Nothing to do.
    }

    @AfterStep
    public void nextStep(Scenario scenario) {
        cucumberStepNumber++;
    }

    private record StepDescription(
            int number,
            String keyword,
            String text
    ) {}

    private StepDescription extractStepDescription(Scenario scenario) throws Exception {
        // Get the delegate from the scenario
        Field delegate = scenario.getClass().getDeclaredField("delegate");
        delegate.setAccessible(true);
        TestCaseState testCaseState = (TestCaseState) delegate.get(scenario);

        // Get the test case from the delegate
        Field testCaseField = testCaseState.getClass().getDeclaredField("testCase");
        testCaseField.setAccessible(true);
        TestCase testCase = (TestCase) testCaseField.get(testCaseState);

        List<PickleStepTestStep> testStepTitles = testCase.getTestSteps()
                .stream()
                .filter(step -> step instanceof PickleStepTestStep)
                .map(step -> (PickleStepTestStep) step)
                .toList();

        PickleStepTestStep pickle = testStepTitles.get(cucumberStepNumber);
        Step step = pickle.getStep();
        return new StepDescription(cucumberStepNumber + 1, step.getKeyword().trim(), step.getText().trim());
    }
}
