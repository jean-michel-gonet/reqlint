package com.reqlint.sandbox.services.tokenbucket;

import java.time.LocalDateTime;

/**
 * Response to a request for tokens availability
 * @param customerId The customer identifier that originated the request.
 * @param availableTokens The number of tokens immediately available.
 * @param replenishmentRate The replenishment rate per second.
 * @param timeStamp The instant when the provided data was read from the system.
 */
public record TokenAvailabilityResponse(
        String customerId,
        int availableTokens,
        int replenishmentRate,
        LocalDateTime timeStamp) {
}
