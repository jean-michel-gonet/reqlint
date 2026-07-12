package com.reqlint.sandbox.services.tokenbucket;

/**
 * A service to grant tokens for customers to consume.
 */
public interface TokenBucketService {
    /**
     * Consumes one token from the specified customer bucket.
     * @param customerId The customer identifier.
     * @return The result of the consumption.
     */
    TokenBucketConsumptionResponse consumeOneToken(String customerId);

    /**
     * Consumes a number of tokens from the specified customer bucket.
     * @param customerId The customer identifier.
     * @return The result of the consumption.
     */
    TokenBucketConsumptionResponse consumeTokens(String customerId, int tokensToConsume);
}
