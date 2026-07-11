package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.services.TokenBucketService;
import io.cucumber.java.en.Given;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenBucketSteps {
    @Autowired
    private TokenBucketService tokenBucketService;

    @Given("Bit Bucket is present")
    public void bit_bucket_is_present() {
        Assertions.assertThat(tokenBucketService).isNotNull();
    }
}
