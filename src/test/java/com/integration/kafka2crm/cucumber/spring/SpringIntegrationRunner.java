package com.integration.kafka2crm.cucumber.spring;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/spring_integration.feature",
    glue = {
        "com.integration.kafka2crm.cucumber.steps",
        "com.integration.kafka2crm.cucumber.spring",
        "com.integration.kafka2crm.cucumber.spring.config",
        "com.integration.kafka2crm.cucumber.config"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/spring-integration.html",
        "json:target/cucumber-reports/spring-integration.json"
    },
    tags = "@spring-integration"
)
public class SpringIntegrationRunner {
} 