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
    TokenBucketResponse consumeOneToken(String customerId);

    /**
     * Consumes a number of tokens from the specified customer bucket.
     * @param customerId The customer identifier.
     * @return The result of the consumption.
     */
    TokenBucketResponse consumeTokens(String customerId, int tokensToConsume);

    /**
     * Obtains the current availability of tokens for the customer.
     * @param customerId The customer identifier.
     * @return The availability response.
     */
    TokenBucketResponse obtainTokenAvailability(String customerId);
}
