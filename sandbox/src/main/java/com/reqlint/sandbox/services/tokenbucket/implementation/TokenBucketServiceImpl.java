package com.reqlint.sandbox.services.tokenbucket.implementation;

import com.reqlint.sandbox.services.ResetEvent;
import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.CustomerService;
import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public TokenBucketResponse consumeOneToken(String customerId) {
        return consumeTokens(customerId, 1);
    }

    @Override
    public TokenBucketResponse consumeTokens(String customerId, int tokensToConsume) {
        LOGGER.info("Customer {} requests to consume {} tokens", customerId, tokensToConsume);
        TokenBucket bucket = obtainBucket(customerId);

        synchronized (bucket) {
            bucket.update(timeService.now());
            int grantedTokens = bucket.consume(tokensToConsume);
            TokenBucketResponse response = new TokenBucketResponse(
                    customerId,
                    bucket.tokenCapacity().intValue(),
                    bucket.refillRate().intValue(),
                    grantedTokens,
                    bucket.availableTokens().intValue(),
                    bucket.computeResetTime(),
                    bucket.lastUpdate());
            if (grantedTokens < tokensToConsume) {
                LOGGER.info("Customer has not enough available tokens: {}", response);
                throw new NotEnoughTokensAvailableException(tokensToConsume, response);
            }
            LOGGER.info("Customer receives requested tokens: {}", response);
            return response;
        }
    }

    @Override
    public TokenBucketResponse obtainTokenAvailability(String customerId) {
        LOGGER.info("Customer {} requests its token availability", customerId);
        TokenBucket bucket = obtainBucket(customerId);

        synchronized (bucket) {
            bucket.update(timeService.now());
            TokenBucketResponse response = new TokenBucketResponse(
                    customerId,
                    bucket.tokenCapacity().intValue(),
                    bucket.refillRate().intValue(),
                    0,
                    bucket.availableTokens().intValue(),
                    bucket.computeResetTime(),
                    bucket.lastUpdate());

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
