package com.reqlint.core.surefire;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SurefireTestReportItemTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String NAME = "NAME";

    @Test
    public void can_segregate_into_stages() {
        SurefireTestReportItem underTest = new SurefireTestReportItem(
                NOW,
                NAME,
                """
                        This part is preparation
                        # Stage 1 - Obtain the bucket capacity of one customer
                        This is content of stage 1
                        # Stage 2 - Compare the bucket capacity of a different customer
                        This is content of stage 2
                        """,
                "");

        Assertions.assertThat(underTest.stageOutputs())
                .containsExactly(
                        new SurefireStageOutputItem(
                                0,
                                "Preparation",
                                "This part is preparation\r\n"),
                        new SurefireStageOutputItem(
                                1,
                                "Obtain the bucket capacity of one customer",
                                "This is content of stage 1\r\n"),
                        new SurefireStageOutputItem(
                                2,
                                "Compare the bucket capacity of a different customer",
                                "This is content of stage 2\r\n"));
    }

}