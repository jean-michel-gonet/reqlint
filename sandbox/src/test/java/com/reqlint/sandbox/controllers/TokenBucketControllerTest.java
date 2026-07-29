package com.reqlint.sandbox.controllers;

import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenBucketController.class)
class TokenBucketControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBucketControllerTest.class);

    private static final String CUSTOMER_ID = "Customer ID";
    private static final int TOKENS_TO_CONSUME = 10;
    private static final int TOKEN_CAPACITY = 100;
    private static final int AVAILABLE_TOKENS = TOKEN_CAPACITY - TOKENS_TO_CONSUME;
    private static final int REFILL_RATE = 20;
    private static final BigDecimal RESET_TIME = BigDecimal.ONE.setScale(1, RoundingMode.UP);
    private static final LocalDateTime TIME_STAMP = LocalDateTime.of(2009, 10, 15, 14, 56, 56);

    private static final TokenBucketResponse RESPONSE =
            new TokenBucketResponse(
                    CUSTOMER_ID,
                    TOKEN_CAPACITY,
                    REFILL_RATE,
                    TOKENS_TO_CONSUME,
                    AVAILABLE_TOKENS,
                    RESET_TIME,
                    TIME_STAMP);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenBucketService tokenBucketService;


    @Test
    public void TC_101_can_consume_tokens() throws Exception {
        Mockito.when(tokenBucketService.consumeTokens(CUSTOMER_ID, TOKENS_TO_CONSUME))
                .thenReturn(RESPONSE);


        mockMvc.perform(MockMvcRequestBuilders.get("/token-bucket/consume/" + CUSTOMER_ID)
                .queryParam("tokensToConsume", String.valueOf(TOKENS_TO_CONSUME)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                .andExpect(jsonPath("$.tokenCapacity").value(String.valueOf(TOKEN_CAPACITY)))
                .andExpect(jsonPath("$.refillRate").value(String.valueOf(REFILL_RATE)))
                .andExpect(jsonPath("$.availableTokens").value(String.valueOf(TOKEN_CAPACITY - TOKENS_TO_CONSUME)))
                .andExpect(jsonPath("$.resetTime").value(String.valueOf(RESET_TIME)))
                .andExpect(jsonPath("$.timeStamp").value(String.valueOf(TIME_STAMP)))
                .andExpect(header().longValue("X-RateLimit-Remaining", AVAILABLE_TOKENS))
                .andExpect(header().longValue("X-RateLimit-Reset", RESET_TIME.multiply(new BigDecimal("1000")).longValue()));
    }

    @Test
    public void can_handle_not_enough_tokens_error() throws Exception {
        Mockito.when(tokenBucketService.consumeTokens(CUSTOMER_ID, TOKENS_TO_CONSUME))
                .thenThrow(new NotEnoughTokensAvailableException(TOKENS_TO_CONSUME, RESPONSE));

        mockMvc.perform(MockMvcRequestBuilders.get("/token-bucket/consume/" + CUSTOMER_ID)
                        .queryParam("tokensToConsume", String.valueOf(TOKENS_TO_CONSUME)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                .andExpect(jsonPath("$.tokenCapacity").value(String.valueOf(TOKEN_CAPACITY)))
                .andExpect(jsonPath("$.refillRate").value(String.valueOf(REFILL_RATE)))
                .andExpect(jsonPath("$.availableTokens").value(String.valueOf(TOKEN_CAPACITY - TOKENS_TO_CONSUME)))
                .andExpect(jsonPath("$.resetTime").value(String.valueOf(RESET_TIME)))
                .andExpect(jsonPath("$.timeStamp").value(String.valueOf(TIME_STAMP)))
                .andExpect(header().longValue("X-RateLimit-Remaining", AVAILABLE_TOKENS))
                .andExpect(header().longValue("X-RateLimit-Reset", RESET_TIME.multiply(new BigDecimal("1000")).longValue()));
    }
}