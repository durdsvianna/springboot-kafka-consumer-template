package com.integration.kafka2crm.kafka;

import com.integration.kafka2crm.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka para mensagens de clientes.
 */
@Slf4j
@Component
public class ClienteConsumer {

    private final ClienteService clienteService;

    /**
     * Construtor para o consumidor de clientes.
     *
     * @param clienteService Serviço para processamento de clientes
     */
    public ClienteConsumer(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Consome mensagens do tópico do Kafka.
     *
     * @param mensagem Mensagem JSON recebida do Kafka
     */
    @KafkaListener(topics = "${kafka.topic.clientes}", groupId = "${kafka.consumer.group-id}")
    public void consumir(String mensagem) {
        log.info("Mensagem recebida do Kafka: {}", mensagem);
        
        try {
            boolean processado = clienteService.processarMensagem(mensagem);
            
            if (processado) {
                log.info("Mensagem processada com sucesso");
            } else {
                log.warn("Falha no processamento da mensagem");
            }
        } catch (Exception e) {
            log.error("Erro ao consumir mensagem do Kafka: {}", e.getMessage(), e);
        }
    }
} 