package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.sandbox.cucumber.beans.DataTableTokenConsumptionResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenConsumptionResponse;

import java.util.Arrays;
import java.util.List;

public class TokenConsumptionResponseTablifier implements Tablifier<TokenConsumptionResponse> {
    @Override
    public List<String> getHeaders() {
        return Arrays.stream(DataTableTokenConsumptionResponse.Field.values()).map(Enum::name).toList();
    }

    @Override
    public List<String> getRow(TokenConsumptionResponse entity) {
        return List.of(
                ValueRenderer.renderString(entity::customerId),
                ValueRenderer.renderInteger(entity::grantedTokens),
                ValueRenderer.renderInteger(entity::availableTokens)
        );
    }
}
