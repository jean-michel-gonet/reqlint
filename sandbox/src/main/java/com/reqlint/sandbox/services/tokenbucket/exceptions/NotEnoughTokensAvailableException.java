package com.reqlint.sandbox.services.tokenbucket.exceptions;

public class NotEnoughTokensAvailableException extends TokenBucketException {
    private final String customerId;
    private final int tokensToConsume;
    private final int availableTokens;

    public NotEnoughTokensAvailableException(String customerId, int tokensToConsume, int availableTokens) {
        super("Not enough tokens available - '"
                + customerId + "' tried to consume "
                + tokensToConsume + ", but only "
                + availableTokens + " are available");
        this.customerId = customerId;
        this.tokensToConsume = tokensToConsume;
        this.availableTokens = availableTokens;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getTokensToConsume() {
        return tokensToConsume;
    }

    public int availableTokens() {
        return availableTokens;
    }
}
