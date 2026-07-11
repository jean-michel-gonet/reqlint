package com.reqlint.sandbox.cucumber;

import com.reqlint.sandbox.services.ServicesConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        CucumberSpringConfiguration.class,
        ServicesConfiguration.class})
@EnableConfigurationProperties
public class CucumberSpringContext {
}