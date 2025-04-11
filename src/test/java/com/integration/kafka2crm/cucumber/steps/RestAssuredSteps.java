package com.integration.kafka2crm.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Steps para testar a integração com o CRM usando Rest Assured
 */
public class RestAssuredSteps {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredSteps.class);
    
    @Value("${crm.api.url}")
    private String crmApiUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final Map<String, Object> mockResponses = new ConcurrentHashMap<>();
    private final AtomicInteger requestCounter = new AtomicInteger(0);
    
    @Before
    public void setup() {
        log.info("Configurando os passos de Rest Assured para API: {}", crmApiUrl);
        mockResponses.clear();
        requestCounter.set(0);
        
        // O restTemplate já é um mock criado pelo Spring Boot Test
        // Não devemos usar reset() aqui, pois o objeto será verificado se é um mock
        
        try {
            URI uri = new URI(crmApiUrl);
            log.info("API path configurado: {}", uri.getPath());
        } catch (Exception e) {
            log.error("Falha ao analisar URL da API CRM: {}", e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        log.info("Limpando configurações de teste");
        mockResponses.clear();
    }
    
    @Given("o CRM está configurado para aceitar requisições")
    public void configureCrmForSuccessfulRequests() {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("status", "success");
        successResponse.put("id", "123-abc");
        
        mockResponses.put("success", successResponse);
        log.info("CRM configurado para retornar resposta de sucesso: {}", successResponse);
        
        // Configurar o mock de RestTemplate para retornar sucesso
        when(restTemplate.exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            any(),
            eq(String.class)
        )).thenReturn(new org.springframework.http.ResponseEntity<>("{\"status\":\"success\",\"id\":\"123-abc\"}", org.springframework.http.HttpStatus.OK));
    }
    
    @Given("o CRM está configurado para retornar erro de servidor")
    public void configureCrmForServerError() {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        
        mockResponses.put("serverError", errorResponse);
        log.info("CRM configurado para simular erro de servidor: {}", errorResponse);
        
        // Configurar o mock de RestTemplate para lançar erro
        when(restTemplate.exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            any(),
            eq(String.class)
        )).thenThrow(new org.springframework.web.client.HttpServerErrorException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));
    }
    
    @Given("o CRM está configurado para rejeitar clientes com dados inválidos")
    public void configureCrmForClientValidationError() {
        Map<String, Object> validationErrorResponse = new HashMap<>();
        validationErrorResponse.put("error", "Invalid client data");
        validationErrorResponse.put("details", "Required fields missing");
        
        mockResponses.put("validationError", validationErrorResponse);
        log.info("CRM configurado para simular erro de validação: {}", validationErrorResponse);
        
        // Configurar o mock de RestTemplate para lançar erro de cliente
        when(restTemplate.exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            any(),
            eq(String.class)
        )).thenThrow(new org.springframework.web.client.HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST));
    }
    
    @Then("uma requisição foi enviada para o CRM")
    public void verifyRequestSentToCrm() {
        requestCounter.incrementAndGet();
        log.info("Verificado que uma requisição foi enviada para o CRM");
        
        // Verificar que o RestTemplate foi chamado pelo menos uma vez
        verify(restTemplate, atLeastOnce()).exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            any(),
            eq(String.class)
        );
    }
    
    @Then("nenhuma requisição foi enviada para o CRM")
    public void verifyNoRequestSentToCrm() {
        assertEquals(0, requestCounter.get(), "Não deveria haver requisições enviadas para o CRM");
        log.info("Verificado que nenhuma requisição foi enviada para o CRM");
        
        // Verificar que o RestTemplate não foi chamado
        verify(restTemplate, never()).exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            any(),
            eq(String.class)
        );
    }
    
    @Then("o corpo da requisição enviada para o CRM contém {string}")
    public void verifyRequestBodyContains(String expectedContent) {
        requestCounter.incrementAndGet();
        log.info("Verificado que o corpo da requisição contém: {}", expectedContent);
        
        // Capturar o argumento enviado ao RestTemplate
        ArgumentCaptor<org.springframework.http.HttpEntity<?>> requestCaptor = ArgumentCaptor.forClass(org.springframework.http.HttpEntity.class);
        verify(restTemplate, atLeastOnce()).exchange(
            eq(crmApiUrl),
            eq(org.springframework.http.HttpMethod.POST),
            requestCaptor.capture(),
            eq(String.class)
        );
        
        // Verificar que o corpo contém o texto esperado
        String requestBody = requestCaptor.getValue().getBody().toString();
        assertTrue(requestBody.contains(expectedContent), 
                   "O corpo da requisição '" + requestBody + "' deveria conter '" + expectedContent + "'");
    }
    
    /**
     * Método para simular uma requisição ao CRM com Rest Assured
     * Este método pode ser usado por outros componentes de teste
     */
    public Response mockCrmRequest(String requestBody, String responseType) {
        RequestSpecification request = given()
            .contentType(ContentType.JSON)
            .body(requestBody);
            
        switch (responseType) {
            case "success":
                return request.expect()
                    .statusCode(HttpStatus.OK.value())
                    .when()
                    .post(crmApiUrl);
            case "serverError":
                return request.expect()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .when()
                    .post(crmApiUrl);
            case "validationError":
                return request.expect()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .when()
                    .post(crmApiUrl);
            default:
                log.warn("Tipo de resposta desconhecido: {}, usando sucesso como padrão", responseType);
                return request.expect()
                    .statusCode(HttpStatus.OK.value())
                    .when()
                    .post(crmApiUrl);
        }
    }
} 