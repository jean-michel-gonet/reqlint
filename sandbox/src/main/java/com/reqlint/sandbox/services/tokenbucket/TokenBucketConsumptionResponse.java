package com.reqlint.sandbox.services.tokenbucket;

/**
 * Response to a request for consuming tokens.
 * @param clientId The client identifier that originated the request.
 * @param grantedTokens The number of granted tokens.
 * @param availableTokes The number of tokens immediately available.
 */
public record TokenBucketConsumptionResponse(
        String clientId,
        int grantedTokens,
        int availableTokes) {

    @Override
    public String toString() {
        return "Customer " + clientId + " consumes " + grantedTokens + " tokens, and is left with " + availableTokes + " available";
    }
}
