package com.integration.kafka2crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.integration.kafka2crm.client.CrmApiClient;
import com.integration.kafka2crm.consumer.ClienteKafkaConsumer;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.service.ClienteMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public CrmApiClient crmApiClient() {
        CrmApiClient mockClient = Mockito.mock(CrmApiClient.class);
        Mockito.when(mockClient.sendClientToCrm(Mockito.any(ClienteCRM.class))).thenReturn(true);
        return mockClient;
    }

    @Bean
    @Primary
    public ClienteMapper clienteMapper() {
        return new ClienteMapper();
    }

    @Bean
    @Primary
    public ClienteKafkaConsumer clienteKafkaConsumer(
            ObjectMapper objectMapper, 
            ClienteMapper clienteMapper,
            CrmApiClient crmApiClient) {
        return new ClienteKafkaConsumer(objectMapper, clienteMapper, crmApiClient);
    }
} 