package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.services.ResetEvent;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

public class ReqlintSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReqlintSteps.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private int cucumberStepNumber = 0;

    @Before
    public void log_scenario_name(Scenario scenario) {
        applicationEventPublisher.publishEvent(new ResetEvent());
        cucumberStepNumber = 0;
        String testIdentifier = extractTestIdentifier(scenario.getUri());
        LOGGER.info("# Prepare {} - {}", testIdentifier, scenario.getName());
    }

    private String extractTestIdentifier(URI uri) {
        String[] segments = uri.toString().split("/");
        return segments[segments.length - 1];
    }

    @BeforeStep
    public void log_step_name(Scenario scenario) throws Exception {
        StepDescription stepDescription = extractStepDescription(scenario);
        if (stepDescription.text().startsWith("Stage")) {
            return;
        }
        LOGGER.info("## Step {} - {}", stepDescription.number(), stepDescription.text());
    }

    @AfterStep
    public void increment_step_number(Scenario scenario) {
        cucumberStepNumber++;
    }

    private record StepDescription(
            int number,
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
        return new StepDescription(cucumberStepNumber + 1, step.getText());
    }

    @Given("Stage {int}")
    public void start_phase(int stepNumber) {
        start_phase(stepNumber, null);
    }

    @Given("Stage {int} - {string}")
    public void start_phase(int stepNumber, String stepName) {
        LOGGER.info("# Stage {} - {}", stepNumber, stepName);
    }

    @Given("Application restarts")
    public void application_restarts() {
        applicationEventPublisher.publishEvent(new ResetEvent());
    }
}
