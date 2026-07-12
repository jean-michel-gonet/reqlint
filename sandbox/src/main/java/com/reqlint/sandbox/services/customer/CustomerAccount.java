package com.reqlint.sandbox.services.customer;

/**
 * A copy of the customer account.
 * @param customerId The customer identifier.
 * @param tokenCapacity The initial token capacity.
 * @param replenishmentRatePerSecond The replenishment rate per second.
 */
public record CustomerAccount(
        String customerId,
        int tokenCapacity,
        int replenishmentRatePerSecond) {
}
