package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.sandbox.cucumber.beans.DataTableNotEnoughTokensAvailableException;
import com.reqlint.sandbox.cucumber.beans.DataTableTokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import io.cucumber.java.bs.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotEnoughTokensAvailableExceptionTablifier implements Tablifier<NotEnoughTokensAvailableException> {
    private final TokenBucketResponseTablifier tokenBucketResponseTablifier = new TokenBucketResponseTablifier();

    @Override
    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add(DataTableNotEnoughTokensAvailableException.TOKENS_TO_CONSUME);
        headers.addAll(tokenBucketResponseTablifier.getHeaders());
        return headers;
    }

    @Override
    public List<String> getRow(NotEnoughTokensAvailableException entity) {
        List<String> values = new ArrayList<>();
        values.add(ValueRenderer.renderInteger(entity::tokensToConsume));
        values.addAll(tokenBucketResponseTablifier.getRow(entity.response()));
        return values;
    }
}
