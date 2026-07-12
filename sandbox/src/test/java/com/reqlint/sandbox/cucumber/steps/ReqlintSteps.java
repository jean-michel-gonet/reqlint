package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.services.ResetEvent;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class ReqlintSteps {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Given("Application restarts")
    public void application_restarts() {
        applicationEventPublisher.publishEvent(new ResetEvent());
    }
}
