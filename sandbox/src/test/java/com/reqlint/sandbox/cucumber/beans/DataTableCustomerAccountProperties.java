package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.customer.properties.CustomerAccountProperties;

import java.util.Map;

public class DataTableCustomerAccountProperties extends CustomerAccountProperties {
    public enum Field {
        CUSTOMER_ID,
        TOKEN_CAPACITY,
        REPLENISHMENT_RATE
    }

    public DataTableCustomerAccountProperties(Map<String, String> entry) {
        setCustomerId(ParameterParser.parseMandatoryString(Field.CUSTOMER_ID, entry));
        setTokenCapacity(ParameterParser.parseMandatoryInteger(Field.TOKEN_CAPACITY, entry));
        setReplenishmentRatePerSecond(ParameterParser.parseMandatoryInteger(Field.REPLENISHMENT_RATE, entry));
    }
}
