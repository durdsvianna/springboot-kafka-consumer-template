package com.integration.kafka2crm.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner for Cucumber tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.integration.kafka2crm.cucumber.steps", "com.integration.kafka2crm.cucumber.config"},
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberTestRunner {
} 