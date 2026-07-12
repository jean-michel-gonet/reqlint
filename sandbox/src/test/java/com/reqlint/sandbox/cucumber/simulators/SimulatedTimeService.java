package com.reqlint.sandbox.cucumber.simulators;

import com.reqlint.sandbox.services.time.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SimulatedTimeService implements TimeService {
    private LocalDateTime now = LocalDateTime.now();

    public void forceTime(LocalTime localTime) {
        now = now.with(localTime);
    }

    @Override
    public LocalDateTime now() {
        return now;
    }
}
