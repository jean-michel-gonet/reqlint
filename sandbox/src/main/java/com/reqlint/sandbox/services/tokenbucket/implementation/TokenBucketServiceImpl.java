package com.reqlint.sandbox.services.tokenbucket.implementation;

import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.CustomerService;
import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketConsumptionResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenBucketServiceImpl implements TokenBucketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBucketServiceImpl.class);

    private final CustomerService customerService;
    private final TimeService timeService;

    private final Map<String, TokenBucket> buckets = new HashMap<>();

    public TokenBucketServiceImpl(CustomerService customerService, TimeService timeService) {
        this.customerService = customerService;
        this.timeService = timeService;
    }

    @Override
    public TokenBucketConsumptionResponse consumeOneToken(String customerId) {
        return consumeTokens(customerId, 1);
    }

    @Override
    public TokenBucketConsumptionResponse consumeTokens(String customerId, int tokensToConsume) {
        LOGGER.info("Customer {} requests to consume {} tokens", customerId, tokensToConsume);
        TokenBucket bucket = obtainBucket(customerId);

        synchronized (bucket) {
            bucket.replenish(timeService.now());
            int availableTokens = bucket.consume(tokensToConsume);
            TokenBucketConsumptionResponse response = new TokenBucketConsumptionResponse(
                    customerId,
                    tokensToConsume,
                    availableTokens);

            LOGGER.info("{}", response);
            return response;
        }
    }

    private TokenBucket obtainBucket(String customerId) {
        TokenBucket tokenBucket;
        synchronized (buckets) {
            tokenBucket = buckets.get(customerId);
            if (tokenBucket == null) {
                CustomerAccount customerAccount = customerService.obtainCustomerAccount(customerId);
                tokenBucket = new TokenBucket(
                        customerId,
                        customerAccount.tokenCapacity(),
                        customerAccount.replenishmentRatePerSecond());
                buckets.put(customerId, tokenBucket);
            }
        }
        return tokenBucket;
    }
}
