package com.integration.kafka2crm.cucumber.spring;

import com.integration.kafka2crm.cucumber.spring.config.SpringTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = SpringTestConfig.class)
public class CucumberSpringConfiguration {
    // Esta classe serve como ponto de entrada para a configuração do Spring nos testes Cucumber
} 