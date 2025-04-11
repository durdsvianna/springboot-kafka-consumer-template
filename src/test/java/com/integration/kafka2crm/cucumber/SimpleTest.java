package com.integration.kafka2crm.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Simple Cucumber test runner that uses Spring integration.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/simple.feature",
    glue = {
        "com.integration.kafka2crm.cucumber.steps",
        "com.integration.kafka2crm.cucumber.spring.config",
        "com.integration.kafka2crm.cucumber.config"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/simple/html/report.html",
        "json:target/cucumber-reports/simple/json/report.json",
        "junit:target/cucumber-reports/simple/junit/report.xml"
    },
    monochrome = true,
    dryRun = false
)
public class SimpleTest {
    // Empty test runner class - configuration is in annotations
} 