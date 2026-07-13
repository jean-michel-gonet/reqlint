package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.tokenbucket.TokenConsumptionResponse;

import java.util.Map;

public class DataTableTokenConsumptionResponse {
    public enum Field {
        CUSTOMER_ID,
        GRANTED_TOKENS,
        AVAILABLE_TOKENS
    }

    public static TokenConsumptionResponse of(Map<String, String> entry) {
        return new TokenConsumptionResponse(
                ParameterParser.parseMandatoryString(Field.CUSTOMER_ID, entry),
                ParameterParser.parseMandatoryInteger(Field.GRANTED_TOKENS, entry.get(Field.GRANTED_TOKENS.name())),
                ParameterParser.parseMandatoryInteger(Field.AVAILABLE_TOKENS, entry.get(Field.AVAILABLE_TOKENS.name())));
    }
}
