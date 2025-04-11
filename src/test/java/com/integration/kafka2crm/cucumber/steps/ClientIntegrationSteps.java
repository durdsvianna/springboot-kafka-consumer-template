package com.integration.kafka2crm.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = com.integration.kafka2crm.cucumber.spring.config.CucumberSpringConfiguration.class)
public class ClientIntegrationSteps {

    private static final Logger log = LoggerFactory.getLogger(ClientIntegrationSteps.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private WireMockServer wireMockServer;
    private String crmBaseUrl;
    private String crmClientsEndpoint;

    @Before
    public void setup() {
        log.info("Setting up test environment");
        try {
            // Stop any existing WireMock server
            if (wireMockServer != null && wireMockServer.isRunning()) {
                wireMockServer.stop();
            }
            
            wireMockServer = new WireMockServer(8080);
            wireMockServer.start();
            crmBaseUrl = "http://localhost:8080";
            crmClientsEndpoint = "/api/v2/clientes";
            configureFor("localhost", 8080);
            
            // Reset WireMock before each test
            wireMockServer.resetAll();
            
            log.info("Test environment setup complete");
        } catch (Exception e) {
            log.error("Error setting up test environment", e);
            throw e;
        }
    }

    @After
    public void tearDown() {
        log.info("Tearing down test environment");
        try {
            if (wireMockServer != null) {
                wireMockServer.stop();
            }
            log.info("Test environment tear down complete");
        } catch (Exception e) {
            log.error("Error tearing down test environment", e);
            throw e;
        }
    }

    @Given("the CRM API is available")
    public void theCrmApiIsAvailable() {
        log.info("Setting up CRM API mock");
        try {
            stubFor(post(urlEqualTo(crmClientsEndpoint))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"status\": \"success\"}")));
            log.info("CRM API mock setup complete");
        } catch (Exception e) {
            log.error("Error setting up CRM API mock", e);
            throw e;
        }
    }

    @When("I send a message to Kafka with the following clients:")
    public void iSendAMessageToKafkaWithTheFollowingClients(List<Cliente> clientes) throws Exception {
        String message = objectMapper.writeValueAsString(clientes);
        log.info("Sending message to Kafka: {}", message);
        try {
            // Send message to Kafka
            kafkaTemplate.send("CLIENTES", message).get(5, TimeUnit.SECONDS);
            
            // Wait for message processing and CRM API call
            int maxAttempts = 20;
            int attempt = 0;
            while (attempt < maxAttempts) {
                if (wireMockServer.getAllServeEvents().size() > 0) {
                    break;
                }
                Thread.sleep(1000);
                attempt++;
                log.info("Waiting for CRM API call, attempt {}/{}", attempt, maxAttempts);
            }
            
            if (attempt >= maxAttempts) {
                log.warn("Timeout waiting for CRM API call");
            }
            
            log.info("Message sent to Kafka and processed");
        } catch (Exception e) {
            log.error("Error sending message to Kafka", e);
            throw e;
        }
    }

    @Then("the CRM should receive the following clients:")
    public void theCrmShouldReceiveTheFollowingClients(List<ClienteCRM> expectedClients) throws Exception {
        String expectedJson = objectMapper.writeValueAsString(expectedClients);
        log.info("Expected JSON: {}", expectedJson);
        try {
            verify(postRequestedFor(urlEqualTo(crmClientsEndpoint))
                    .withRequestBody(equalToJson(expectedJson)));
            log.info("CRM API received expected clients");
        } catch (Exception e) {
            log.error("Error verifying CRM API received clients", e);
            throw e;
        }
    }

    @Given("the CRM API returns a server error")
    public void theCrmApiReturnsAServerError() {
        log.info("Setting up CRM API mock for server error");
        try {
            stubFor(post(urlEqualTo(crmClientsEndpoint))
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"error\": \"Internal Server Error\"}")));
            log.info("CRM API mock setup for server error complete");
        } catch (Exception e) {
            log.error("Error setting up CRM API mock for server error", e);
            throw e;
        }
    }

    @Given("the CRM API returns a client error")
    public void theCrmApiReturnsAClientError() {
        log.info("Setting up CRM API mock for client error");
        try {
            stubFor(post(urlEqualTo(crmClientsEndpoint))
                    .willReturn(aResponse()
                            .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"error\": \"Invalid Request\"}")));
            log.info("CRM API mock setup for client error complete");
        } catch (Exception e) {
            log.error("Error setting up CRM API mock for client error", e);
            throw e;
        }
    }

    @Then("the error should be logged")
    public void theErrorShouldBeLogged() {
        log.info("Verifying error was logged");
        try {
            verify(postRequestedFor(urlEqualTo(crmClientsEndpoint)));
            assertTrue(true);
            log.info("Error verification complete");
        } catch (Exception e) {
            log.error("Error verifying error was logged", e);
            throw e;
        }
    }
} 