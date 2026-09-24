package com.reqlint.core.model.specification.items;

import com.reqlint.core.model.specification.items.TestCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCaseTest {
    private static final String CHILD_OF_1 = "1";
    private static final String CHILD_OF_2 = "2";

    private TestCase underTest;

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new TestCase();
    }

    @Test
    public void can_do_something_nice() {
        underTest.childOf(CHILD_OF_1);
        underTest.childOf(CHILD_OF_2);
        Assertions.assertThat(underTest.childOf()).containsExactly(CHILD_OF_1, CHILD_OF_2);
    }
}
