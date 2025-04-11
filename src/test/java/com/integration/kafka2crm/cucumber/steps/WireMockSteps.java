package com.integration.kafka2crm.cucumber.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class WireMockSteps {

    private static final Logger log = LoggerFactory.getLogger(WireMockSteps.class);
    
    @Autowired
    private WireMockServer wireMockServer;
    
    @Value("${crm.api.url}")
    private String crmApiUrl;
    
    private String apiPath;
    
    @Before
    public void setup() {
        log.info("Setting up WireMock steps");
        if (!wireMockServer.isRunning()) {
            log.warn("WireMock server was not running. Starting it now.");
            wireMockServer.start();
        }
        wireMockServer.resetAll();
        
        try {
            URI uri = new URI(crmApiUrl);
            apiPath = uri.getPath();
            log.info("WireMock server is running on port {} and will mock path {}", wireMockServer.port(), apiPath);
        } catch (Exception e) {
            log.error("Failed to parse CRM API URL: {}", e.getMessage());
            apiPath = "/api/v2/clientes";
        }
    }
    
    @After
    public void tearDown() {
        log.info("Cleaning up WireMock stubs");
        wireMockServer.resetAll();
    }
    
    @Given("o CRM está configurado para aceitar requisições")
    public void configureCrmForSuccessfulRequests() {
        wireMockServer.stubFor(post(urlEqualTo(apiPath))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"status\":\"success\",\"id\":\"123-abc\"}")));
        log.info("Configured CRM mock to accept client requests with success response");
    }
    
    @Given("o CRM está configurado para retornar erro de servidor")
    public void configureCrmForServerError() {
        wireMockServer.stubFor(post(urlEqualTo(apiPath))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"error\":\"Internal server error\"}")));
        log.info("Configured CRM mock to return server error (500)");
    }
    
    @Given("o CRM está configurado para rejeitar clientes com dados inválidos")
    public void configureCrmForClientValidationError() {
        wireMockServer.stubFor(post(urlEqualTo(apiPath))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"error\":\"Invalid client data\",\"details\":\"Required fields missing\"}")));
        log.info("Configured CRM mock to reject clients with invalid data (400)");
    }
    
    @Then("uma requisição foi enviada para o CRM")
    public void verifyRequestSentToCrm() throws InterruptedException {
        boolean requestFound = false;
        int maxAttempts = 20;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            List<LoggedRequest> requests = wireMockServer.findAll(postRequestedFor(urlEqualTo(apiPath)));
            if (!requests.isEmpty()) {
                log.info("Found {} request(s) to CRM at path {}", requests.size(), apiPath);
                requestFound = true;
                break;
            }
            log.info("No requests to CRM found yet. Waiting... (attempt {}/{})", attempt + 1, maxAttempts);
            TimeUnit.MILLISECONDS.sleep(300);
        }
        
        assertTrue(requestFound, "Expected at least one request to CRM but none was recorded");
    }
    
    @Then("nenhuma requisição foi enviada para o CRM")
    public void verifyNoRequestSentToCrm() throws InterruptedException {
        // Wait a bit to make sure no requests come in
        TimeUnit.SECONDS.sleep(1);
        
        List<LoggedRequest> requests = wireMockServer.findAll(postRequestedFor(urlEqualTo(apiPath)));
        assertEquals(0, requests.size(), "Expected no requests to CRM but found " + requests.size());
        log.info("Verified that no request was sent to CRM");
    }
    
    @Then("o corpo da requisição enviada para o CRM contém {string}")
    public void verifyRequestBodyContains(String expectedContent) throws InterruptedException {
        boolean requestFound = false;
        int maxAttempts = 20;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            List<LoggedRequest> requests = wireMockServer.findAll(postRequestedFor(urlEqualTo(apiPath)));
            if (!requests.isEmpty()) {
                String requestBody = new String(requests.get(0).getBody());
                log.info("Request body: {}", requestBody);
                assertTrue(requestBody.contains(expectedContent), 
                    "Expected request body to contain '" + expectedContent + "' but was: " + requestBody);
                requestFound = true;
                break;
            }
            log.info("No requests to CRM found yet. Waiting... (attempt {}/{})", attempt + 1, maxAttempts);
            TimeUnit.MILLISECONDS.sleep(300);
        }
        
        assertTrue(requestFound, "Expected at least one request to CRM but none was recorded");
    }
} 