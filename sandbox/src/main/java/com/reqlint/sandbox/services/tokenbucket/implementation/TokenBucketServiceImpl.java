package com.reqlint.sandbox.services.tokenbucket.implementation;

import com.reqlint.sandbox.services.ResetEvent;
import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.CustomerService;
import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.tokenbucket.TokenAvailabilityResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenConsumptionResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenBucketServiceImpl implements TokenBucketService, ApplicationListener<ResetEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBucketServiceImpl.class);

    private final CustomerService customerService;
    private final TimeService timeService;

    private final Map<String, TokenBucket> buckets = new HashMap<>();

    public TokenBucketServiceImpl(CustomerService customerService, TimeService timeService) {
        this.customerService = customerService;
        this.timeService = timeService;
    }

    @Override
    public TokenConsumptionResponse consumeOneToken(String customerId) {
        return consumeTokens(customerId, 1);
    }

    @Override
    public TokenConsumptionResponse consumeTokens(String customerId, int tokensToConsume) {
        LOGGER.info("Customer {} requests to consume {} tokens", customerId, tokensToConsume);
        TokenBucket bucket = obtainBucket(customerId);

        synchronized (bucket) {
            bucket.replenish(timeService.now());
            int availableTokens = bucket.consume(tokensToConsume);
            TokenConsumptionResponse response = new TokenConsumptionResponse(
                    customerId,
                    tokensToConsume,
                    availableTokens);

            LOGGER.info("{}", response);
            return response;
        }
    }

    @Override
    public TokenAvailabilityResponse obtainTokenAvailability(String customerId) {
        LOGGER.info("Customer {} requests its token availability", customerId);
        TokenBucket bucket = obtainBucket(customerId);

        synchronized (bucket) {
            bucket.replenish(timeService.now());
            TokenAvailabilityResponse response = new TokenAvailabilityResponse(
                    customerId,
                    bucket.getAvailableTokens().intValue(),
                    bucket.getReplenishmentRate().intValue(),
                    bucket.getLastUpdate());

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
                        customerAccount.replenishmentRate());
                buckets.put(customerId, tokenBucket);
            }
        }
        return tokenBucket;
    }

    @Override
    public void onApplicationEvent(ResetEvent event) {
        synchronized (buckets) {
            buckets.clear();
        }
    }
}
