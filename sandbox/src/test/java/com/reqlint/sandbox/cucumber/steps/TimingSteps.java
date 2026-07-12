package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.cucumber.simulators.SimulatedTimeService;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimingSteps {
    private static final Pattern LOCAL_TIME_PATTERN = Pattern
            .compile("([0-9]{2}):?([0-9]{2})?:?([0-9]{2})?\\.?([0-9]+)?");

    @Autowired
    private SimulatedTimeService timeService;

    @ParameterType("[0-9:.]+")
    public LocalTime localTime(String sLocalTime) {
        Matcher matcher = LOCAL_TIME_PATTERN.matcher(sLocalTime);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(sLocalTime + " is not a correctly formatted local time. It should be as '13h25:34.456'");
        }
        String sHour = matcher.group(1);
        int hour = Integer.parseInt(sHour);

        String sMinute = matcher.group(2);
        int minute = sMinute == null ? 0 : Integer.parseInt(sMinute);

        String sSecond = matcher.group(3);
        int second = sSecond == null ? 0 : Integer.parseInt(sSecond);

        String sDecimals = matcher.group(4);
        int nanos;
        if (sDecimals == null) {
            nanos = 0;
        } else {
            int l = sDecimals.length();
            BigDecimal decimals = new BigDecimal(sDecimals)
                    .multiply(BigDecimal.TEN.pow(9))
                    .divide(BigDecimal.TEN.pow(l), RoundingMode.FLOOR);
            nanos = decimals.intValueExact();
        }

        return LocalTime.of(hour, minute, second, nanos);
    }

    @Given("Now is {localTime}")
    public void now_is_local_time(LocalTime localTime) {
        timeService.forceTime(localTime);
    }
}
