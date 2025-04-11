package com.integration.kafka2crm.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.service.ClienteMappingService;
import com.integration.kafka2crm.service.CrmApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Kafka consumer for the CLIENTES topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final ClienteMappingService clienteMappingService;
    private final CrmApiService crmApiService;

    /**
     * Consumes messages from the CLIENTES topic.
     *
     * @param message The JSON message containing client(s)
     */
    @KafkaListener(topics = "${kafka.topic.clientes}", groupId = "${kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("Received message: {}", message);
        
        try {
            // Parse the message into a list of Cliente objects
            List<Cliente> clientes = parseMessage(message);
            
            if (clientes.isEmpty()) {
                log.info("No clients to process in the message");
                return;
            }
            
            // Process each client
            for (Cliente cliente : clientes) {
                try {
                    // Map the client to CRM format
                    var clienteCRM = clienteMappingService.mapToCRM(cliente);
                    
                    // Send to CRM API
                    var response = crmApiService.sendToCRM(clienteCRM);
                    log.info("Client {} successfully sent to CRM. Response: {}", cliente.getId(), response);
                } catch (Exception e) {
                    log.error("Error processing client {}: {}", cliente.getId(), e.getMessage(), e);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing message: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Parses the JSON message into a list of Cliente objects.
     *
     * @param message The JSON message
     * @return List of Cliente objects
     * @throws JsonProcessingException if parsing fails
     */
    private List<Cliente> parseMessage(String message) throws JsonProcessingException {
        try {
            return objectMapper.readValue(message, new TypeReference<List<Cliente>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse client list from message: {}", e.getMessage());
            throw e;
        }
    }
} 