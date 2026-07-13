package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.cucumber.beans.ParameterParser;
import com.reqlint.sandbox.cucumber.simulators.SimulatedTimeService;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

public class TimingSteps {

    @Autowired
    private SimulatedTimeService timeService;

    @ParameterType("[0-9:.]+")
    public LocalTime localTime(String sLocalTime) {
        return ParameterParser.parseMandatoryLocalTime("localTime", sLocalTime);
    }

    @Given("Now is {localTime}")
    public void now_is_local_time(LocalTime localTime) {
        timeService.forceTime(localTime);
    }
}
