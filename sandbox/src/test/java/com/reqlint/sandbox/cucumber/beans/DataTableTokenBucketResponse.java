package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;

import java.time.LocalDateTime;
import java.util.Map;

public class DataTableTokenBucketResponse {
    public enum Field {
        CUSTOMER_ID,
        TOKEN_CAPACITY,
        REFILL_RATE,
        GRANTED_TOKENS,
        AVAILABLE_TOKENS,
        RESET_TIME,
        TIME_STAMP
    }

    public static TokenBucketResponse of(LocalDateTime now, Map<String, String> entry) {
        return new TokenBucketResponse(
                ParameterParser.parseMandatoryString(Field.CUSTOMER_ID, entry),
                ParameterParser.parseMandatoryInteger(Field.TOKEN_CAPACITY, entry),
                ParameterParser.parseMandatoryInteger(Field.REFILL_RATE, entry),
                ParameterParser.parseMandatoryInteger(Field.GRANTED_TOKENS, entry),
                ParameterParser.parseMandatoryInteger(Field.AVAILABLE_TOKENS, entry),
                ParameterParser.parseMandatoryBigDecimal(Field.RESET_TIME, entry),
                now.with(ParameterParser.parseMandatoryLocalTime(Field.TIME_STAMP, entry)));
    }
}
