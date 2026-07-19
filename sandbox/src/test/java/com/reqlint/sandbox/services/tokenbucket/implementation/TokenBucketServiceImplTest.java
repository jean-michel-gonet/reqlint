package com.reqlint.sandbox.services.tokenbucket.implementation;

import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.CustomerService;
import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class TokenBucketServiceImplTest {
    private static final String CUSTOMER_1_CLIENT_ID = "CUSTOMER1";
    private static final Integer CUSTOMER_1_TOKEN_CAPACITY = 10;
    private static final Integer CUSTOMER_1_REFILL_RATE = 20;

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final Duration HALF_A_SECOND = Duration.of(500, ChronoUnit.MILLIS);
    private static final Duration FIVE_SECONDS = Duration.ofSeconds(5);

    private TokenBucketServiceImpl underTest;

    private TimeService timeService;

    @BeforeEach
    public void setUp() {
        CustomerService customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.obtainCustomerAccount(CUSTOMER_1_CLIENT_ID))
                .thenReturn(new CustomerAccount(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE));

        timeService = Mockito.mock(TimeService.class);
        Mockito.when(timeService.now()).thenReturn(NOW);

        underTest = new TokenBucketServiceImpl(customerService, timeService);
    }

    @Test
    public void can_deplete_the_initial_token_capacity() {
        int consumedTokens = 0;
        for(int expectedAvailableTokens = CUSTOMER_1_TOKEN_CAPACITY - 1; expectedAvailableTokens >= 0; expectedAvailableTokens--) {
            consumedTokens++;
            Assertions.assertThat(underTest.consumeOneToken(CUSTOMER_1_CLIENT_ID))
                    .isEqualTo(new TokenBucketResponse(
                            CUSTOMER_1_CLIENT_ID,
                            CUSTOMER_1_TOKEN_CAPACITY,
                            CUSTOMER_1_REFILL_RATE,
                            1,
                            expectedAvailableTokens,
                            BigDecimal.valueOf(consumedTokens)
                                    .divide(BigDecimal.valueOf(CUSTOMER_1_REFILL_RATE), 3, RoundingMode.UP),
                            NOW));
        }

        Assertions.assertThatExceptionOfType(NotEnoughTokensAvailableException.class)
                .isThrownBy(() -> underTest.consumeOneToken(CUSTOMER_1_CLIENT_ID));
    }

    @Test
    public void can_update_available_tokens_when_time_passes() {
        Assertions.assertThat(underTest.consumeTokens(CUSTOMER_1_CLIENT_ID, CUSTOMER_1_TOKEN_CAPACITY))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        0,
                        BigDecimal.ONE.divide(BigDecimal.TWO, 3, RoundingMode.UP),
                        NOW));

        Mockito.when(timeService.now()).thenReturn(NOW.plus(HALF_A_SECOND));

        Assertions.assertThat(underTest.consumeTokens(CUSTOMER_1_CLIENT_ID, CUSTOMER_1_REFILL_RATE / 2))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        CUSTOMER_1_REFILL_RATE / 2,
                        0,
                        BigDecimal.ONE.divide(BigDecimal.TWO, 3, RoundingMode.UP),
                        NOW.plus(HALF_A_SECOND)));
    }

    @Test
    public void can_update_up_to_token_capacity() {
        Assertions.assertThat(underTest.consumeTokens(CUSTOMER_1_CLIENT_ID, CUSTOMER_1_TOKEN_CAPACITY))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        0,
                        BigDecimal.ONE.divide(BigDecimal.TWO, 3, RoundingMode.UP),
                        NOW));

        Mockito.when(timeService.now()).thenReturn(NOW.plus(FIVE_SECONDS));

        Assertions.assertThat(underTest.consumeTokens(CUSTOMER_1_CLIENT_ID, CUSTOMER_1_TOKEN_CAPACITY))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        0,
                        BigDecimal.ONE.divide(BigDecimal.TWO, 3, RoundingMode.UP),
                        NOW.plus(FIVE_SECONDS)));
    }

    @Test
    public void can_obtain_the_token_availability() {
        Assertions.assertThat(underTest.obtainTokenAvailability(CUSTOMER_1_CLIENT_ID))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        0,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        BigDecimal.ZERO.setScale(3, RoundingMode.UP),
                        NOW));

        underTest.consumeTokens(CUSTOMER_1_CLIENT_ID, CUSTOMER_1_TOKEN_CAPACITY);

        Assertions.assertThat(underTest.obtainTokenAvailability(CUSTOMER_1_CLIENT_ID))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        0,
                        0,
                        BigDecimal.valueOf(CUSTOMER_1_TOKEN_CAPACITY)
                                .divide(BigDecimal.valueOf(CUSTOMER_1_REFILL_RATE), 3, RoundingMode.UP),
                        NOW));

        Mockito.when(timeService.now()).thenReturn(NOW.plus(HALF_A_SECOND));

        Assertions.assertThat(underTest.obtainTokenAvailability(CUSTOMER_1_CLIENT_ID))
                .isEqualTo(new TokenBucketResponse(
                        CUSTOMER_1_CLIENT_ID,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REFILL_RATE,
                        0,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        BigDecimal.ZERO.setScale(3, RoundingMode.UP),
                        NOW.plus(HALF_A_SECOND)));
    }
}