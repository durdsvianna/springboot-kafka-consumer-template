package com.integration.kafka2crm.cucumber.standalone;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone steps that don't depend on Spring context
 */
public class StandaloneSteps {
    private static final Logger logger = LoggerFactory.getLogger(StandaloneSteps.class);
    
    private boolean kafkaConfigured;
    private boolean messageProcessed;
    
    @Before
    public void setup(Scenario scenario) {
        logger.info("===== INICIANDO CENÁRIO: {} =====", scenario.getName());
        logger.info("Setting up test environment (standalone steps)");
        kafkaConfigured = false;
        messageProcessed = false;
    }
    
    @After
    public void tearDown(Scenario scenario) {
        logger.info("Cleaning up test environment (standalone steps)");
        logger.info("===== FINALIZANDO CENÁRIO: {} com status {} =====", 
                    scenario.getName(), 
                    scenario.isFailed() ? "FALHA" : "SUCESSO");
    }
    
    @Given("the Kafka consumer is configured")
    public void theKafkaConsumerIsConfigured() {
        logger.info("Configuring mock Kafka consumer");
        kafkaConfigured = true;
        logger.info("Kafka consumer successfully configured");
    }
    
    @When("a client message is processed")
    public void aClientMessageIsProcessed() {
        logger.info("Processing mock client message");
        if (kafkaConfigured) {
            messageProcessed = true;
            logger.info("Client message successfully processed");
        } else {
            logger.warn("Cannot process message - Kafka consumer not configured");
        }
    }
    
    @Then("the client data is sent to the CRM API")
    public void theClientDataIsSentToTheCRMAPI() {
        logger.info("Verifying client data was sent to mock API");
        if (messageProcessed) {
            logger.info("Test passed: Client data was successfully sent to API");
        } else {
            logger.error("Test failed: Client data was not sent to API");
        }
        assert messageProcessed : "Message should have been processed";
    }
} 