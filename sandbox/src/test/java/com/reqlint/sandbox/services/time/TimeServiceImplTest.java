package com.reqlint.sandbox.services.time;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class TimeServiceImplTest {
    private TimeServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new TimeServiceImpl();
    }
    @Test
    public void can_give_time() {
        Assertions.assertThat(underTest.now())
                .isCloseTo(LocalDateTime.now(),
                        new TemporalUnitWithinOffset(100, ChronoUnit.MILLIS));
    }

}