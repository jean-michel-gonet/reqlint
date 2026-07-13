package com.reqlint.sandbox.services.time;

import java.time.LocalDateTime;

/**
 * Indirect access to local system time, to ease testing.
 */
public interface TimeService {
    LocalDateTime now();
}
