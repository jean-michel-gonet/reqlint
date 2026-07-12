package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.cucumber.beans.DataTableNotEnoughTokensAvailableException;
import com.reqlint.sandbox.cucumber.beans.DataTableTokenConsumptionResponse;
import com.reqlint.sandbox.cucumber.renderers.MarkDownFormatter;
import com.reqlint.sandbox.cucumber.renderers.NotEnoughTokensAvailableExceptionTablifier;
import com.reqlint.sandbox.cucumber.renderers.TokenConsumptionResponseTablifier;
import com.reqlint.sandbox.services.tokenbucket.TokenConsumptionResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.it.Ma;
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
    private TokenBucketService tokenBucketService;

    @DataTableType
    public TokenConsumptionResponse tokenConsumptionResponse(Map<String, String> entry) {
        return DataTableTokenConsumptionResponse.of(entry);
    }

    @DataTableType
    public NotEnoughTokensAvailableException notEnoughTokensAvailableException(Map<String, String> entry) {
        return new DataTableNotEnoughTokensAvailableException(entry);
    }

    @Given("Customer {string} succeeds in consuming {int} tokens with response")
    public void customer_succeeds_in_consuming_tokens(String customerId, int tokensToConsume, List<TokenConsumptionResponse> expected) {
        LOGGER.info("Expect following response: {}",
                new MarkDownFormatter<>(expected, new TokenConsumptionResponseTablifier()));

        TokenConsumptionResponse actual = tokenBucketService.consumeTokens(customerId, tokensToConsume);
        try {
            Assertions.assertThat(expected.getFirst()).isEqualTo(actual);
            LOGGER.info("Response is as expected");
        } catch (AssertionFailedError e) {
            LOGGER.error("Actual response is:  {}",
                    new MarkDownFormatter<>(actual, new TokenConsumptionResponseTablifier()));
            throw e;
        }
    }

    @When("Customer {string} fails to consume {int} tokens with error")
    public void customerFailsToConsumeTokensWithError(String customerId, int tokensToConsume, List<NotEnoughTokensAvailableException> expected) {
        LOGGER.info("Customer should receive the following error: {}",
                new MarkDownFormatter<>(expected, new NotEnoughTokensAvailableExceptionTablifier()));
        try {
            TokenConsumptionResponse response = tokenBucketService.consumeTokens(customerId, tokensToConsume);
            LOGGER.error("Customer succeeded in consuming tokens: {}", new MarkDownFormatter<>(response, new TokenConsumptionResponseTablifier()));
            Assertions.fail("The customer was able to consume the tokens");
        } catch (NotEnoughTokensAvailableException actual) {
            LOGGER.info("Customer was prevented to consume tokens with the following exception:\n{}",
                    new MarkDownFormatter<>(actual, new NotEnoughTokensAvailableExceptionTablifier()));
            Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected.getFirst());
        }
    }
}
