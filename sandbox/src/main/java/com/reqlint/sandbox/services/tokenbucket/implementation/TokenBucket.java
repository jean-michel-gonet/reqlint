package com.reqlint.sandbox.services.tokenbucket.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Contains the logic for one bucket.
 */
class TokenBucket {
    private static final BigDecimal MILLISECOND = new BigDecimal("0.001");
    private final BigDecimal tokenCapacity;
    private final BigDecimal refillRate;

    private BigDecimal availableTokens;
    private LocalDateTime lastUpdate;

    /**
     * Class constructor.
     * @param tokenCapacity The maximum number of tokens available.
     * @param refillRate The replenishment rate per second.
     */
    public TokenBucket(Integer tokenCapacity, Integer refillRate) {
        this.tokenCapacity = BigDecimal.valueOf(tokenCapacity);
        this.availableTokens = BigDecimal.valueOf(tokenCapacity);
        this.refillRate = BigDecimal.valueOf(refillRate);
    }

    /**
     * Replenishes this bucket at the specified time stamp.
     * The number of tokens added is calculated based on the {@link #lastUpdate}, the provided
     * time stamp, and the {@link #refillRate}.
     * @param timeStamp The time stamp.
     */
    public void update(LocalDateTime timeStamp) {
        if (lastUpdate != null) {
            Duration durationSinceLastConsumption = Duration.between(lastUpdate, timeStamp);
            BigDecimal millisSinceLastConsumption = BigDecimal.valueOf(durationSinceLastConsumption.toMillis());
            BigDecimal replenishedTokens = refillRate
                    .multiply(millisSinceLastConsumption)
                    .multiply(MILLISECOND);

            BigDecimal potentiallyAvailableTokens = availableTokens.add(replenishedTokens);
            availableTokens = potentiallyAvailableTokens.min(tokenCapacity);
        }
        lastUpdate = timeStamp;
    }

    /**
     * Consumes the specified number of tokens at the specified time stamp.
     * The call succeeds if {@link #availableTokens} is greater or equal to the tokens to consume.
     * If the call does not succeed, then it returns zero.
     * Remember to call {@link #update} just before calling this method.
     * @param tokensToConsume The number of tokens.
     * @return The number consumed tokens.
     */
    public int consume(int tokensToConsume) {
        BigDecimal bdTokensToConsume = BigDecimal.valueOf(tokensToConsume);
        if (bdTokensToConsume.compareTo(availableTokens) > 0) {
            return 0;
        }
        availableTokens = availableTokens.subtract(bdTokensToConsume);
        return tokensToConsume;
    }

    /**
     * Computes the number of seconds to wait, considering the {@link #refillRate},
     * until the {@link #availableTokens} reach the {@link #tokenCapacity}.
     * @return The number of seconds to wait until available tokens is reset.
     */
    public BigDecimal computeResetTime() {
        BigDecimal tokensToRefill = tokenCapacity.subtract(availableTokens);
        return tokensToRefill.divide(refillRate, 3, RoundingMode.UP);
    }

    public BigDecimal refillRate() {
        return refillRate;
    }

    public BigDecimal availableTokens() {
        return availableTokens;
    }

    public LocalDateTime lastUpdate() {
        return lastUpdate;
    }

    public BigDecimal tokenCapacity() {
        return tokenCapacity;
    }
}
