package com.integration.kafka2crm.service;

import com.integration.kafka2crm.model.ClienteCRM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to communicate with the CRM API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmApiService {

    private final RestTemplate restTemplate;
    
    @Value("${crm.api.url}")
    private String crmApiUrl;
    
    /**
     * Sends client data to the CRM API.
     *
     * @param clienteCRM The client data to send
     * @return The API response as a string
     * @throws RuntimeException if an error occurs during communication
     */
    public String sendToCRM(ClienteCRM clienteCRM) {
        log.info("Sending client data to CRM API: {}", clienteCRM.getExternalId());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<ClienteCRM> request = new HttpEntity<>(clienteCRM, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                crmApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully sent client data to CRM. Status: {}", response.getStatusCode());
                return response.getBody();
            } else {
                log.warn("Unexpected response from CRM API. Status: {}", response.getStatusCode());
                throw new RuntimeException("Unexpected response from CRM API: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error when communicating with CRM API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Error in request to CRM API: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("Server error from CRM API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("CRM API server error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error communicating with CRM API", e);
            throw new RuntimeException("Error communicating with CRM API: " + e.getMessage(), e);
        }
    }
} 