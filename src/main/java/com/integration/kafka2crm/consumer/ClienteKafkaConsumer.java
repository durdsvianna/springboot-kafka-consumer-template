package com.integration.kafka2crm.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.client.CrmApiClient;
import com.integration.kafka2crm.service.ClienteMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Consumidor Kafka responsável por processar mensagens do tópico CLIENTES.
 */
@Component
public class ClienteKafkaConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteKafkaConsumer.class);
    
    private final ObjectMapper objectMapper;
    private final ClienteMapper clienteMapper;
    private final CrmApiClient crmApiClient;
    
    public ClienteKafkaConsumer(
            ObjectMapper objectMapper,
            ClienteMapper clienteMapper,
            CrmApiClient crmApiClient) {
        this.objectMapper = objectMapper;
        this.clienteMapper = clienteMapper;
        this.crmApiClient = crmApiClient;
    }
    
    /**
     * Processa mensagens do tópico Kafka CLIENTES.
     * 
     * @param message Mensagem JSON recebida do Kafka
     */
    @KafkaListener(topics = "${kafka.topic.clientes}", groupId = "${kafka.consumer.group-id}")
    public void consumeClienteMessage(String message) {
        logger.info("Received message from Kafka: {}", message);
        
        try {
            // Desserializa a mensagem JSON para uma lista de clientes
            List<Cliente> clientes = objectMapper.readValue(
                    message, 
                    new TypeReference<List<Cliente>>() {}
            );
            
            logger.info("Processed {} clients from message", clientes.size());
            
            // Para cada cliente, mapeia para o formato do CRM e envia
            clientes.forEach(this::processAndSendClienteToCrm);
            
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON message: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing message: {}", e.getMessage());
        }
    }
    
    /**
     * Processa um cliente e envia para o CRM.
     * 
     * @param cliente Cliente a ser processado
     */
    private void processAndSendClienteToCrm(Cliente cliente) {
        try {
            logger.info("Processing client: {}", cliente.getId());
            
            // Mapeia para o formato do CRM
            ClienteCRM clienteCRM = clienteMapper.mapToCRM(cliente);
            
            // Envia para o CRM
            boolean success = crmApiClient.sendClientToCrm(clienteCRM);
            
            if (success) {
                logger.info("Client {} successfully processed", cliente.getId());
            } else {
                logger.warn("Failed to process client {}", cliente.getId());
            }
            
        } catch (CrmApiClient.CrmServerException e) {
            logger.error("Server error when sending client to CRM: {}", e.getMessage());
        } catch (CrmApiClient.CrmClientException e) {
            logger.error("Client error when sending client to CRM: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error when processing client: {}", e.getMessage());
        }
    }
} 