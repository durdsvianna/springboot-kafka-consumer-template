package com.integration.kafka2crm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${crm.api.base-url}")
    private String crmBaseUrl;

    @Value("${crm.api.clients-endpoint}")
    private String crmClientsEndpoint;

    public void sendToCrm(List<Cliente> clientes) {
        try {
            List<ClienteCRM> clientesCRM = clientes.stream()
                    .map(this::toClienteCRM)
                    .collect(Collectors.toList());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<ClienteCRM>> request = new HttpEntity<>(clientesCRM, headers);
            String url = crmBaseUrl + crmClientsEndpoint;

            log.info("Sending clients to CRM: {}", clientesCRM);
            restTemplate.postForObject(url, request, String.class);
            log.info("Clients sent successfully to CRM");
        } catch (Exception e) {
            log.error("Error sending clients to CRM", e);
            throw e;
        }
    }

    private ClienteCRM toClienteCRM(Cliente cliente) {
        return ClienteCRM.builder()
                .externalId(cliente.getId())
                .fullName(cliente.getNome())
                .email(cliente.getEmail())
                .phone(cliente.getTelefone())
                .build();
    }
} 