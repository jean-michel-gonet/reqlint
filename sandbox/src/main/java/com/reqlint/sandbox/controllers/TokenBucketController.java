package com.reqlint.sandbox.controllers;

import com.reqlint.sandbox.services.tokenbucket.TokenBucketResponse;
import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping(value = "/token-bucket")
public class TokenBucketController {
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private final TokenBucketService tokenBucketService;

    public TokenBucketController(TokenBucketService tokenBucketService) {
        this.tokenBucketService = tokenBucketService;
    }

    @GetMapping(value = "consume/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenBucketResponse> consume(
            @PathVariable(name = "customerId") String customerId,
            @RequestParam(name = "tokensToConsume") Integer tokensToConsume) {

        TokenBucketResponse response = tokenBucketService.consumeTokens(customerId, tokensToConsume);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Remaining", String.valueOf(response.availableTokens()));
        headers.add("X-RateLimit-Reset", String.valueOf(response.resetTime().multiply(THOUSAND).longValue()));

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @ExceptionHandler(NotEnoughTokensAvailableException.class)
    public ResponseEntity<TokenBucketResponse> handleNotEnoughTokensAvailable(NotEnoughTokensAvailableException exception) {
        TokenBucketResponse response = exception.response();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Remaining", String.valueOf(response.availableTokens()));
        headers.add("X-RateLimit-Reset", String.valueOf(response.resetTime().multiply(THOUSAND).longValue()));

        return new ResponseEntity<>(response, headers, HttpStatus.TOO_MANY_REQUESTS);
    }
}
