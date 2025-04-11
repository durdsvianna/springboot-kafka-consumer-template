package com.integration.kafka2crm.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.service.CrmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteConsumer {

    private final ObjectMapper objectMapper;
    private final CrmService crmService;

    @KafkaListener(topics = "CLIENTES", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            log.info("Received message: {}", message);
            List<Cliente> clientes = objectMapper.readValue(message, new TypeReference<List<Cliente>>() {});
            crmService.sendToCrm(clientes);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
} 