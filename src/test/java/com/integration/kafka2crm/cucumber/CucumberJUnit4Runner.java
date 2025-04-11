package com.integration.kafka2crm.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner para testes Cucumber com JUnit 4 usando a abordagem standalone.
 * Usa a mesma configuração que o SimpleTest, mas com saída de relatórios separada.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.integration.kafka2crm.cucumber.steps",
        "com.integration.kafka2crm.cucumber.standalone",
        "com.integration.kafka2crm.cucumber.spring",
        "com.integration.kafka2crm.cucumber.spring.config",
        "com.integration.kafka2crm.cucumber.config"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-pretty",
        "json:target/cucumber-reports/CucumberTestReport.json"
    },
    monochrome = true
)
public class CucumberJUnit4Runner {
    // This class serves as a test runner for Cucumber with JUnit 4
} 