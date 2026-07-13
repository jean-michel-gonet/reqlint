package com.reqlint.sandbox.cucumber.beans;

import com.reqlint.sandbox.services.tokenbucket.TokenAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.Map;

public class DataTableTokenAvailabilityResponse {
    public enum Field {
        CUSTOMER_ID,
        AVAILABLE_TOKENS,
        REPLENISHMENT_RATE,
        TIME_STAMP
    }

    public static TokenAvailabilityResponse of(LocalDateTime now, Map<String, String> entry) {
        return new TokenAvailabilityResponse(
                ParameterParser.parseMandatoryString(Field.CUSTOMER_ID, entry),
                ParameterParser.parseMandatoryInteger(Field.AVAILABLE_TOKENS, entry),
                ParameterParser.parseMandatoryInteger(Field.REPLENISHMENT_RATE, entry),
                now.with(ParameterParser.parseMandatoryLocalTime(Field.TIME_STAMP, entry)));
    }
}
