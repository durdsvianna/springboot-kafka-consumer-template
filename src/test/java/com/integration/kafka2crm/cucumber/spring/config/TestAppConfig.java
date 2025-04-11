package com.integration.kafka2crm.cucumber.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.Kafka2CrmApplication;
import com.integration.kafka2crm.consumer.ClienteConsumer;
import com.integration.kafka2crm.service.CrmService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@ComponentScan(basePackageClasses = {Kafka2CrmApplication.class})
public class TestAppConfig {

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
    public CrmService crmService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        return new CrmService(restTemplate, objectMapper);
    }

    @Bean
    @Primary
    public ClienteConsumer clienteConsumer(ObjectMapper objectMapper, CrmService crmService) {
        return new ClienteConsumer(objectMapper, crmService);
    }
} 