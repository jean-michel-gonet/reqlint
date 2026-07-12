package com.reqlint.sandbox.services.tokenbucket.exceptions;

import java.math.BigDecimal;

public class NotEnoughTokensAvailableException extends TokenBucketException {
    public NotEnoughTokensAvailableException(BigDecimal tokensToConsume, BigDecimal availableTokens) {
        super("Not enough tokens available - tried to consume "
                + tokensToConsume + ", but only "
                + availableTokens + " are available");
    }
}
