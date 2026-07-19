package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.cucumber.beans.DataTableNotEnoughTokensAvailableException;
import com.reqlint.sandbox.cucumber.beans.DataTableTokenBucketResponse;
import com.reqlint.sandbox.cucumber.renderers.MarkDownFormatter;
import com.reqlint.sandbox.cucumber.renderers.NotEnoughTokensAvailableExceptionTablifier;
import com.reqlint.sandbox.cucumber.renderers.TokenBucketResponseTablifier;
import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import org.assertj.core.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class TokenBucketSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBucketSteps.class);

    @Autowired
    public TokenBucketService tokenBucketService;

    @Autowired
    public TimeService timeService;

    @DataTableType
    public TokenBucketResponse tokenBucketResponse(Map<String, String> entry) {
        return DataTableTokenBucketResponse.of(timeService.now(), entry);
    }

    @DataTableType
    public NotEnoughTokensAvailableException notEnoughTokensAvailableException(Map<String, String> entry) {
        return new DataTableNotEnoughTokensAvailableException(timeService.now(), entry);
    }

    @Given("Customer {string} succeeds in consuming {int} tokens with response")
    public void customer_succeeds_in_consuming_tokens(String customerId, int tokensToConsume, List<TokenBucketResponse> expected) {
        LOGGER.info("Expect following response: {}",
                new MarkDownFormatter<>(expected, new TokenBucketResponseTablifier()));

        TokenBucketResponse actual = tokenBucketService.consumeTokens(customerId, tokensToConsume);
        try {
            Assertions.assertThat(expected.getFirst()).isEqualTo(actual);
            LOGGER.info("Response is as expected");
        } catch (AssertionFailedError e) {
            LOGGER.error("Actual response is:  {}",
                    new MarkDownFormatter<>(actual, new TokenBucketResponseTablifier()));
            throw e;
        }
    }

    @Given("Customer {string} fails to consume {int} tokens with error")
    public void customer_fails_to_consume_tokens(String customerId, int tokensToConsume, List<NotEnoughTokensAvailableException> expected) {
        LOGGER.info("{}", new MarkDownFormatter<>(expected, new NotEnoughTokensAvailableExceptionTablifier()));
        try {
            TokenBucketResponse response = tokenBucketService.consumeTokens(customerId, tokensToConsume);
            LOGGER.error("Customer succeeded in consuming tokens: {}", new MarkDownFormatter<>(response, new TokenBucketResponseTablifier()));
            Assertions.fail("The customer was able to consume the tokens");
        } catch (NotEnoughTokensAvailableException actual) {
            LOGGER.info("Customer was prevented to consume tokens with the following exception:\n{}",
                    new MarkDownFormatter<>(actual, new NotEnoughTokensAvailableExceptionTablifier()));
            Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected.getFirst());
        }
    }

    @Given("Customer {string} has the following token availability")
    public void customer_has_the_following_availability(String customerId, List<TokenBucketResponse> expected) {
        LOGGER.info("{}", new MarkDownFormatter<>(expected, new TokenBucketResponseTablifier()));
        TokenBucketResponse actual = tokenBucketService.obtainTokenAvailability(customerId);
        try {
            Assertions.assertThat(expected.getFirst()).isEqualTo(actual);
            LOGGER.info("Availability is as expected");
        } catch (AssertionFailedError e) {
            LOGGER.error("Actual availability is: {}",
                    new MarkDownFormatter<>(actual, new TokenBucketResponseTablifier()));
            throw e;
        }
    }
}
