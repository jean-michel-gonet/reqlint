package com.reqlint.sandbox.services.tokenbucket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response to a request for tokens availability
 * @param customerId The customer identifier that originated the request.
 * @param tokenCapacity The maximum number of tokens, before consuming any.
 * @param refillRate The replenishment rate per second.
 * @param grantedTokens The number of tokens granted in this request.
 * @param availableTokens The number of tokens immediately available, after consuming the granted tokens.
 * @param resetTime The number of seconds until tokens are refilled to capacity.
 * @param timeStamp The instant when the provided data was read from the system.
 */
public record TokenBucketResponse(
        String customerId,
        int tokenCapacity,
        int refillRate,
        int grantedTokens,
        int availableTokens,
        BigDecimal resetTime,
        LocalDateTime timeStamp) {
}
