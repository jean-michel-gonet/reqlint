package com.reqlint.sandbox.controllers;

import com.reqlint.sandbox.services.tokenbucket.TokenBucketService;
import com.reqlint.sandbox.services.tokenbucket.TokenConsumptionResponse;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/token-bucket")
public class TokenBucketController {
    private final TokenBucketService tokenBucketService;

    public TokenBucketController(TokenBucketService tokenBucketService) {
        this.tokenBucketService = tokenBucketService;
    }

    @GetMapping(value = "consume/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenConsumptionResponse> consume(
            @PathVariable(name = "customerId") String customerId,
            @RequestParam(name = "tokensToConsume") Integer tokensToConsume) {

        TokenConsumptionResponse response = tokenBucketService.consumeTokens(customerId, tokensToConsume);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Remaining", String.valueOf(response.availableTokens()));
        headers.add("X-RateLimit-Reset", String.valueOf(response.availableTokens()));

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @ExceptionHandler(NotEnoughTokensAvailableException.class)
    public ResponseEntity<NotEnoughTokensAvailableException> handleNotEnoughTokensAvailable(NotEnoughTokensAvailableException response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Remaining", String.valueOf(response.availableTokens()));
        headers.add("X-RateLimit-Reset", String.valueOf(response.availableTokens()));

        return new ResponseEntity<>(response, headers, HttpStatus.TOO_MANY_REQUESTS);
    }
}
