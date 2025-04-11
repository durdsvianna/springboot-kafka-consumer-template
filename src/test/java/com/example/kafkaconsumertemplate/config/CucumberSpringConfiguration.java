package com.example.kafkaconsumertemplate.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {SpringTestConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class CucumberSpringConfiguration {
    // This class is intentionally empty as it's just a configuration class
} 