package com.integration.kafka2crm;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.integration.kafka2crm.cucumber.steps.simple", "com.integration.kafka2crm.cucumber"},
    plugin = {"pretty", "html:target/cucumber-reports"},
    dryRun = false,
    monochrome = true
)
public class CucumberSimpleTest {
    // Esta classe é apenas um runner para os testes Cucumber
} 