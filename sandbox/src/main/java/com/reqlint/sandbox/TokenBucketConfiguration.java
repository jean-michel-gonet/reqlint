package com.reqlint.sandbox;

import com.reqlint.sandbox.services.time.TimeService;
import com.reqlint.sandbox.services.time.TimeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenBucketConfiguration {
    @Bean
    TimeService timeService() {
        return new TimeServiceImpl();
    }
}
