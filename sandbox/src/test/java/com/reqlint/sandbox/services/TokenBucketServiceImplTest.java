package com.reqlint.sandbox.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenBucketServiceImplTest {
    private TokenBucketServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new TokenBucketServiceImpl();
    }

    @Test
    public void can_do_something_nice() {
        Assertions.assertThat(underTest).isNotNull();
    }
}