package com.integration.kafka2crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Kafka to CRM integration microservice.
 */
@SpringBootApplication
public class Kafka2CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(Kafka2CrmApplication.class, args);
    }
} 