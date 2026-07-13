package com.reqlint.sandbox.services.tokenbucket;

/**
 * Response to a request for consuming tokens.
 * @param customerId The customer identifier that originated the request.
 * @param grantedTokens The number of granted tokens.
 * @param availableTokes The number of tokens immediately available.
 */
public record TokenConsumptionResponse(
        String customerId,
        int grantedTokens,
        int availableTokes) {

    @Override
    public String toString() {
        return "Customer " + customerId + " consumes " + grantedTokens + " tokens, and is left with " + availableTokes + " available";
    }
}
