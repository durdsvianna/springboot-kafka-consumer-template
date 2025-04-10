package com.integration.kafka2crm.client;

import com.integration.kafka2crm.model.ClienteCRM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente para comunicação com a API do CRM.
 */
@Slf4j
@Component
public class CRMClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientsEndpoint;

    /**
     * Construtor para o cliente CRM.
     *
     * @param restTemplate RestTemplate para as requisições HTTP
     * @param baseUrl URL base da API do CRM
     * @param clientsEndpoint Endpoint para os clientes no CRM
     */
    public CRMClient(RestTemplate restTemplate,
                     @Value("${crm.api.base-url}") String baseUrl,
                     @Value("${crm.api.clients-endpoint}") String clientsEndpoint) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientsEndpoint = clientsEndpoint;
    }

    /**
     * Envia um cliente para o CRM.
     *
     * @param clienteCRM o cliente no formato do CRM
     * @return true se o envio foi bem-sucedido, false caso contrário
     */
    public boolean enviarCliente(ClienteCRM clienteCRM) {
        try {
            String url = baseUrl + clientsEndpoint;
            log.info("Enviando cliente com ID externo {} para o CRM: {}", clienteCRM.getExternalId(), url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<ClienteCRM> request = new HttpEntity<>(clienteCRM, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Cliente com ID externo {} enviado com sucesso para o CRM", clienteCRM.getExternalId());
                return true;
            } else {
                log.error("Erro ao enviar cliente com ID externo {} para o CRM. Status: {}", 
                        clienteCRM.getExternalId(), response.getStatusCode());
                return false;
            }
        } catch (RestClientException e) {
            log.error("Exceção ao enviar cliente com ID externo {} para o CRM: {}", 
                    clienteCRM.getExternalId(), e.getMessage(), e);
            return false;
        }
    }
} 