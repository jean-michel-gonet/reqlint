package com.reqlint.sandbox.services.time;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Default implementation for {@link TimeService}.
 */
@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
