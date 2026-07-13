package com.reqlint.sandbox.services.tokenbucket.implementation;

import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Contains the logic for one bucket.
 *
 */
class TokenBucket {
    private static final BigDecimal MILLISECOND = new BigDecimal("0.001");
    private final String customerId;
    private final BigDecimal tokenCapacity;
    private final BigDecimal replenishmentRate;

    private BigDecimal availableTokens;
    private LocalDateTime lastUpdate;

    /**
     * Class constructor.
     * @param customerId The identifier of the customer owning the bucket.
     * @param tokenCapacity The maximum number of tokens available.
     * @param replenishmentRate The replenishment rate per second.
     */
    public TokenBucket(String customerId, Integer tokenCapacity, Integer replenishmentRate) {
        this.customerId = customerId;
        this.tokenCapacity = BigDecimal.valueOf(tokenCapacity);
        this.availableTokens = BigDecimal.valueOf(tokenCapacity);
        this.replenishmentRate = BigDecimal.valueOf(replenishmentRate);
    }

    /**
     * Replenishes this bucket at the specified time stamp.
     * The number of tokens added is calculated based on the {@link #lastUpdate}, the provided
     * time stamp, and the {@link #replenishmentRate}.
     * @param timeStamp The time stamp.
     */
    public void replenish(LocalDateTime timeStamp) {
        if (lastUpdate != null) {
            Duration durationSinceLastConsumption = Duration.between(lastUpdate, timeStamp);
            BigDecimal millisSinceLastConsumption = BigDecimal.valueOf(durationSinceLastConsumption.toMillis());
            BigDecimal replenishedTokens = replenishmentRate
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
     * Remember to call {@link #replenish} just before calling this method.
     * @param tokensToConsume The number of tokens.
     * @return The number of available tokens, after consuming the requested number of tokens.
     */
    public int consume(int tokensToConsume) {
        BigDecimal bdTokensToConsume = BigDecimal.valueOf(tokensToConsume);
        if (bdTokensToConsume.compareTo(availableTokens) > 0) {
            throw new NotEnoughTokensAvailableException(
                    customerId,
                    bdTokensToConsume.intValue(),
                    availableTokens.intValue());
        }
        availableTokens = availableTokens.subtract(bdTokensToConsume);
        return availableTokens.intValue();
    }

    public BigDecimal getReplenishmentRate() {
        return replenishmentRate;
    }

    public BigDecimal getAvailableTokens() {
        return availableTokens;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
