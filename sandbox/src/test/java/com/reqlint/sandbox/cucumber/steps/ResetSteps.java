package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.services.ResetEvent;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class ResetSteps {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Before
    public void log_scenario_name(Scenario scenario) {
        applicationEventPublisher.publishEvent(new ResetEvent());
    }
}
