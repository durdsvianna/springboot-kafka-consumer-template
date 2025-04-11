package com.integration.kafka2crm.client;

import com.integration.kafka2crm.model.ClienteCRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente para comunicação com a API do CRM.
 */
@Component
public class CrmApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CrmApiClient.class);
    
    private final RestTemplate restTemplate;
    private final String crmApiUrl;
    
    public CrmApiClient(
            RestTemplate restTemplate,
            @Value("${crm.api.base-url}") String baseUrl,
            @Value("${crm.api.clients-endpoint}") String clientsEndpoint) {
        this.restTemplate = restTemplate;
        this.crmApiUrl = baseUrl + clientsEndpoint;
        logger.info("CRM API Client initialized with URL: {}", crmApiUrl);
    }
    
    /**
     * Envia um cliente para o CRM.
     *
     * @param clienteCRM Cliente a ser enviado
     * @return true se o envio foi bem-sucedido, false caso contrário
     * @throws CrmServerException Se ocorrer um erro de servidor (5xx)
     * @throws CrmClientException Se ocorrer um erro de cliente (4xx)
     */
    public boolean sendClientToCrm(ClienteCRM clienteCRM) {
        try {
            logger.info("Sending client to CRM: {}", clienteCRM.getExternalId());
            
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    crmApiUrl,
                    clienteCRM,
                    Void.class
            );
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            
            if (success) {
                logger.info("Client successfully sent to CRM: {}", clienteCRM.getExternalId());
            } else {
                logger.warn("Unexpected response from CRM: {}", response.getStatusCode());
            }
            
            return success;
        } catch (HttpServerErrorException e) {
            logger.error("Server error when sending client to CRM: {}", e.getMessage());
            throw new CrmServerException("Error from CRM server: " + e.getStatusCode(), e);
        } catch (HttpClientErrorException e) {
            logger.error("Client error when sending client to CRM: {}", e.getMessage());
            throw new CrmClientException("Error in request to CRM: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Unexpected error when sending client to CRM: {}", e.getMessage());
            throw new CrmServerException("Unexpected error communicating with CRM", e);
        }
    }
    
    /**
     * Exceção para erros de servidor (5xx) do CRM.
     */
    public static class CrmServerException extends RuntimeException {
        public CrmServerException(String message) {
            super(message);
        }
        
        public CrmServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exceção para erros de cliente (4xx) do CRM.
     */
    public static class CrmClientException extends RuntimeException {
        public CrmClientException(String message) {
            super(message);
        }
        
        public CrmClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 