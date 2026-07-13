package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.sandbox.cucumber.beans.DataTableTokenAvailabilityResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenAvailabilityResponse;

import java.util.Arrays;
import java.util.List;

public class TokenAvailabilityResponseTablifier implements Tablifier<TokenAvailabilityResponse> {
    @Override
    public List<String> getHeaders() {
        return Arrays.stream(DataTableTokenAvailabilityResponse.Field.values()).map(Enum::name).toList();
    }

    @Override
    public List<String> getRow(TokenAvailabilityResponse entity) {
        return List.of(
                ValueRenderer.renderString(entity::customerId),
                ValueRenderer.renderInteger(entity::availableTokens),
                ValueRenderer.renderInteger(entity::replenishmentRate),
                ValueRenderer.renderLocalTime(entity::timeStamp));
    }
}
