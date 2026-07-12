package com.reqlint.sandbox.services.customer.properties;

public class CustomerAccountProperties {
    private String customerId;
    private Integer tokenCapacity;
    private Integer replenishmentRatePerSecond;

    public String getCustomerId() {
        return customerId;
    }

    public CustomerAccountProperties setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public Integer getTokenCapacity() {
        return tokenCapacity;
    }

    public CustomerAccountProperties setTokenCapacity(Integer tokenCapacity) {
        this.tokenCapacity = tokenCapacity;
        return this;
    }

    public Integer getReplenishmentRatePerSecond() {
        return replenishmentRatePerSecond;
    }

    public CustomerAccountProperties setReplenishmentRatePerSecond(Integer replenishmentRatePerSecond) {
        this.replenishmentRatePerSecond = replenishmentRatePerSecond;
        return this;
    }
}
