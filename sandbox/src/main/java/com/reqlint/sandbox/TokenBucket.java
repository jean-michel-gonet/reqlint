package com.reqlint.sandbox;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackageClasses = TokenBucketConfiguration.class)
public class TokenBucket {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TokenBucket.class)
                .run(args);
    }
}
