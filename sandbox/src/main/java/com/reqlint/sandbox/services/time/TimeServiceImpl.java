package com.reqlint.sandbox.services.time;

import java.time.LocalDateTime;

/**
 * Default implementation for {@link TimeService}.
 */
public class TimeServiceImpl implements TimeService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
