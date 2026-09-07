package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.core.output.surefire.Tablifier;
import com.reqlint.sandbox.cucumber.beans.DataTableTokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;

import java.util.Arrays;
import java.util.List;

public class TokenBucketResponseTablifier implements Tablifier<TokenBucketResponse> {
    @Override
    public List<String> getHeaders() {
        return Arrays.stream(DataTableTokenBucketResponse.Field.values()).map(Enum::name).toList();
    }

    @Override
    public List<String> getRow(TokenBucketResponse entity) {
        return List.of(
            ValueRenderer.renderString(entity::customerId),
            ValueRenderer.renderInteger(entity::tokenCapacity),
            ValueRenderer.renderInteger(entity::refillRate),
            ValueRenderer.renderInteger(entity::grantedTokens),
            ValueRenderer.renderInteger(entity::availableTokens),
            ValueRenderer.renderBigDecimal(entity::resetTime),
            ValueRenderer.renderLocalTime(entity::timeStamp));
    }
}
