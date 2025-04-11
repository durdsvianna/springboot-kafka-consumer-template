package com.integration.kafka2crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.consumer.ClienteConsumer;
import com.integration.kafka2crm.service.CrmService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public CrmService crmService() {
        return Mockito.mock(CrmService.class);
    }

    @Bean
    @Primary
    public ClienteConsumer clienteConsumer() {
        return Mockito.mock(ClienteConsumer.class);
    }
} 