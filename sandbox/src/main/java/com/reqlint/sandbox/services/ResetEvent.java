package com.reqlint.sandbox.services;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * This event is propagated during test execution, so services can reset their state between tests.
 */
public class ResetEvent extends ApplicationEvent {
    public ResetEvent() {
        super(LocalDateTime.now());
    }
}
