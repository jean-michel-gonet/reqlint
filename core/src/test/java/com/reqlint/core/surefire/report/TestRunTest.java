package com.reqlint.core.surefire.report;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

class TestRunTest {
    private static final String TEST_NAME = "TEST_NAME";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String LEADING_STUFF = "2026-08-13 09:29:28,267 INFO  c.s.t.s.m.t.s.s.r.w.ClassName:666 - ";

    private static final String LOG_ENTRY_1 = "Something has happened deep below, although the surface remains calm";
    private static final String LOG_ENTRY_2 = "Small ripples are visible on the surface";
    private static final String LOG_ENTRY_3 = "As they reach the shallows, ripples become huge waves";
    private static final String LOG_ENTRY_4 = "Run! Now!";
    private static final String LOG_ENTRY_5 = "All is desolation";
    private static final String LOG_ENTRY_6 = "Chaos and death";

    private static final String STAGE_1_NAME = "First Stage";
    private static final String STAGE_1 = "Stage 1 - " + STAGE_1_NAME;
    private static final String STAGE_2_NAME = "Second Stage is also important";
    private static final String STAGE_2 = "Stage 2 - " + STAGE_2_NAME;

    private static final String STEP_1 = "Do this in step one";
    private static final String STEP_2 = "Do that in step two";
    private static final String STEP_3 = "Don't forget this in step three";
    private static final String STEP_4 = "Don't forget that in step four";
    private static final String STEP_5 = "Verify this in step five";
    private static final String STEP_6 = "Verify that in step six";

    private static final String GIVEN = "Given";
    private static final String THEN = "Then";
    private static final String WHEN = "When";
    private static final String AND = "And";
    private static final String STAR = "*";

    @Test
    public void can_parse_a_test_with_only_preparation() {
        TestRun underTest = TestRun.builder()
                .timeStamp(NOW)
                .name(TEST_NAME)
                .output(
                        LEADING_STUFF + "Some entries",
                        LEADING_STUFF + "More entries",
                        LEADING_STUFF + LogPatternsAndFormats.preparationOf("Feature", "Scenario"),
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(1, GIVEN, STEP_1),
                        LEADING_STUFF + LOG_ENTRY_1,
                        LEADING_STUFF + LOG_ENTRY_2,
                        LEADING_STUFF + LOG_ENTRY_3,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(2, STAR, STEP_2),
                        LEADING_STUFF + LOG_ENTRY_4,
                        LEADING_STUFF + LOG_ENTRY_5,
                        LEADING_STUFF + LOG_ENTRY_6)
                .build();

        var expectedPreparation = new TestRunStage(
                0,
                "Preparation",
                List.of(
                        new TestRunStageStep(
                                1,
                                STEP_1,
                                List.of(LEADING_STUFF + LOG_ENTRY_1,
                                        LEADING_STUFF + LOG_ENTRY_2,
                                        LEADING_STUFF + LOG_ENTRY_3),
                                null),
                        new TestRunStageStep(
                                2,
                                STEP_2,
                                List.of(LEADING_STUFF + LOG_ENTRY_4,
                                        LEADING_STUFF + LOG_ENTRY_5,
                                        LEADING_STUFF + LOG_ENTRY_6),
                                null)),
                Collections.emptyList());

        Assertions.assertThat(underTest.preparation()).isEqualTo(expectedPreparation);
        Assertions.assertThat(underTest.stages()).isEmpty();
        Assertions.assertThat(underTest.isFailed()).isFalse();
    }

    @Test
    @Disabled
    public void can_parse_a_test_with_no_preparation_and_two_stages() {
        TestRun underTest = TestRun.builder()
                .timeStamp(NOW)
                .name(TEST_NAME)
                .output(
                        LEADING_STUFF + "Some entries",
                        LEADING_STUFF + "More entries",
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(3, GIVEN, STAGE_1),
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(4, GIVEN, STEP_1),
                        LEADING_STUFF + LOG_ENTRY_1,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(5, WHEN, STEP_2),
                        LEADING_STUFF + LOG_ENTRY_2,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(6, THEN, STEP_3),
                        LEADING_STUFF + LOG_ENTRY_3,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(7, AND, STEP_4),
                        LEADING_STUFF + LOG_ENTRY_4,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(3, GIVEN, STAGE_2),
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(4, GIVEN, STEP_5),
                        LEADING_STUFF + LOG_ENTRY_5,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(6, THEN, STEP_6),
                        LEADING_STUFF + LOG_ENTRY_6)
                .build();

        Assertions.assertThat(underTest.title()).isEqualTo(TEST_NAME);
        Assertions.assertThat(underTest.timeStamp()).isEqualTo(NOW);

        Assertions.assertThat(underTest.preparation()).isNull();

        var expectedStage1 = new TestRunStage(
                1,
                STAGE_1_NAME,
                List.of(
                        new TestRunStageStep(
                                4,
                                STEP_3,
                                List.of(LEADING_STUFF + LOG_ENTRY_3),
                                null),
                        new TestRunStageStep(
                                5,
                                STEP_4,
                                List.of(LEADING_STUFF + LOG_ENTRY_4),
                                null)),
                List.of(
                        new TestRunStageStep(
                                6,
                                STEP_5,
                                List.of(LEADING_STUFF + LOG_ENTRY_5),
                                null),
                        new TestRunStageStep(
                                7,
                                STEP_6,
                                List.of(LEADING_STUFF + LOG_ENTRY_6),
                                null)));

        var expectedStage2 = new TestRunStage(
                2,
                STAGE_2_NAME,
                List.of(
                        new TestRunStageStep(
                                4,
                                STEP_3,
                                List.of(LEADING_STUFF + LOG_ENTRY_3),
                                null),
                        new TestRunStageStep(
                                5,
                                STEP_4,
                                List.of(LEADING_STUFF + LOG_ENTRY_4),
                                null)),
                List.of(
                        new TestRunStageStep(
                                6,
                                STEP_5,
                                List.of(LEADING_STUFF + LOG_ENTRY_5),
                                null),
                        new TestRunStageStep(
                                7,
                                STEP_6,
                                List.of(LEADING_STUFF + LOG_ENTRY_6),
                                null)));

        Assertions.assertThat(underTest.stages()).containsExactly(expectedStage1, expectedStage2);

        Assertions.assertThat(underTest.isFailed()).isFalse();
    }

    @Test
    public void can_parse_a_test_with_one_preparation_and_one_stage() {
        TestRun underTest = TestRun.builder()
                .timeStamp(NOW)
                .name(TEST_NAME)
                .output(
                        LEADING_STUFF + "Some entries",
                        LEADING_STUFF + "More entries",
                        LEADING_STUFF + LogPatternsAndFormats.preparationOf("Feature", "Scenario"),
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(1, GIVEN, STEP_1),
                        LEADING_STUFF + LOG_ENTRY_1,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(2, AND, STEP_2),
                        LEADING_STUFF + LOG_ENTRY_2,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(3, GIVEN, STAGE_1),
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(4, GIVEN, STEP_3),
                        LEADING_STUFF + LOG_ENTRY_3,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(5, WHEN, STEP_4),
                        LEADING_STUFF + LOG_ENTRY_4,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(6, THEN, STEP_5),
                        LEADING_STUFF + LOG_ENTRY_5,
                        LEADING_STUFF + LogPatternsAndFormats.stepOf(7, AND, STEP_6),
                        LEADING_STUFF + LOG_ENTRY_6)
                .build();

        Assertions.assertThat(underTest.title()).isEqualTo(TEST_NAME);
        Assertions.assertThat(underTest.timeStamp()).isEqualTo(NOW);

        var expectedPreparation = new TestRunStage(
                0,
                "Preparation",
                List.of(
                        new TestRunStageStep(
                                1,
                                STEP_1,
                                List.of(LEADING_STUFF + LOG_ENTRY_1),
                                null),
                        new TestRunStageStep(
                                2,
                                STEP_2,
                                List.of(LEADING_STUFF + LOG_ENTRY_2),
                                null)),
                Collections.emptyList());
        Assertions.assertThat(underTest.preparation()).isEqualTo(expectedPreparation);

        var expectedStage = new TestRunStage(
                1,
                STAGE_1_NAME,
                List.of(
                        new TestRunStageStep(
                                4,
                                STEP_3,
                                List.of(LEADING_STUFF + LOG_ENTRY_3),
                                null),
                        new TestRunStageStep(
                                5,
                                STEP_4,
                                List.of(LEADING_STUFF + LOG_ENTRY_4),
                                null)),
                List.of(
                        new TestRunStageStep(
                                6,
                                STEP_5,
                                List.of(LEADING_STUFF + LOG_ENTRY_5),
                                null),
                        new TestRunStageStep(
                                7,
                                STEP_6,
                                List.of(LEADING_STUFF + LOG_ENTRY_6),
                                null)));

        Assertions.assertThat(underTest.stages()).containsExactly(expectedStage);

        Assertions.assertThat(underTest.isFailed()).isFalse();
    }
}