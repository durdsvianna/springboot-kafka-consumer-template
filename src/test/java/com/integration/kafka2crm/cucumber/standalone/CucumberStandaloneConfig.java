package com.integration.kafka2crm.cucumber.standalone;

import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

/**
 * Configuração básica para testes Cucumber sem o Spring Boot.
 * Esta classe é apenas uma configuração vazia para satisfazer a exigência do Cucumber.
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberStandaloneConfig.class)
public class CucumberStandaloneConfig {
    private static final Logger logger = LoggerFactory.getLogger(CucumberStandaloneConfig.class);
    
    @Bean
    public String testRunId() {
        String id = java.util.UUID.randomUUID().toString();
        logger.info("Iniciando configuração de teste com ID: {}", id);
        return id;
    }
} 