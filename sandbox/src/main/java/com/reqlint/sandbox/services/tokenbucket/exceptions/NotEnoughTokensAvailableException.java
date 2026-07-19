package com.reqlint.sandbox.services.tokenbucket.exceptions;

import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;

/**
 * The customer tried to consume more tokens than available.
 */
public class NotEnoughTokensAvailableException extends TokenBucketException {
    private final TokenBucketResponse response;
    private final int tokensToConsume;

    /**
     * Class constructor.
     * @param tokensToConsume The number of tokens the customer tried to consume.
     * @param response The details of the response.
     */
    public NotEnoughTokensAvailableException(int tokensToConsume, TokenBucketResponse response) {
        super("Not enough tokens available - '"
                + response.customerId() + "' tried to consume "
                + tokensToConsume + ", but only "
                + response.availableTokens() + " are available");
        this.tokensToConsume = tokensToConsume;
        this.response = response;
    }

    public int tokensToConsume() {
        return tokensToConsume;
    }

    public TokenBucketResponse response() {
        return response;
    }
}
