package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;

import java.time.LocalDateTime;
import java.util.Map;

public class DataTableNotEnoughTokensAvailableException extends NotEnoughTokensAvailableException {
    public static final String TOKENS_TO_CONSUME = "TOKENS_TO_CONSUME";

    public DataTableNotEnoughTokensAvailableException(LocalDateTime now, Map<String, String> entry) {
        super(ParameterParser.parseMandatoryInteger(TOKENS_TO_CONSUME, entry.get(TOKENS_TO_CONSUME)),
                DataTableTokenBucketResponse.of(now, entry));
    }
}
