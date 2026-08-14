package com.reqlint.core.surefire;

import com.reqlint.core.surefire.report.TestRunStage;
import com.reqlint.core.surefire.report.TestRun;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;

class SurefireTestReportItemTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String NAME = "NAME";

    @org.junit.jupiter.api.Test
    public void can_segregate_into_stages() {
        TestRun underTest = TestRun.builder()
                .timeStamp(NOW)
                .name(NAME)
                .output("""
                        This part is preparation
                        # Stage 1 - Obtain the bucket capacity of one customer
                        This is content of stage 1
                        # Stage 2 - Compare the bucket capacity of a different customer
                        This is content of stage 2
                        """)
                .build();

        /*
        Assertions.assertThat(underTest.stages())
                .containsExactly(
                        new TestRunStage(
                                0,
                                "Preparation",
                                "This part is preparation\r\n"),
                        new TestRunStage(
                                1,
                                "Obtain the bucket capacity of one customer",
                                "This is content of stage 1\r\n"),
                        new TestRunStage(
                                2,
                                "Compare the bucket capacity of a different customer",
                                "This is content of stage 2\r\n"));
         */
        Assertions.fail("Do your testing");
    }

}