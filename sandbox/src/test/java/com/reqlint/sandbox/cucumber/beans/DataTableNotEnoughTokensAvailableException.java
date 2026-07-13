package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;

import java.util.Map;

public class DataTableNotEnoughTokensAvailableException extends NotEnoughTokensAvailableException {
    public enum Field {
        CUSTOMER_ID,
        TOKENS_TO_CONSUME,
        AVAILABLE_TOKENS
    }

    public DataTableNotEnoughTokensAvailableException(Map<String, String> entry) {
        super(ParameterParser.parseMandatoryString(Field.CUSTOMER_ID, entry),
                ParameterParser.parseMandatoryInteger(Field.TOKENS_TO_CONSUME, entry),
                ParameterParser.parseMandatoryInteger(Field.AVAILABLE_TOKENS, entry));
    }
}
