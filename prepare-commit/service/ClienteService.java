package com.integration.kafka2crm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.client.CRMClient;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço para processamento de clientes e envio para o CRM.
 */
@Slf4j
@Service
public class ClienteService {

    private final ObjectMapper objectMapper;
    private final ClienteMapper clienteMapper;
    private final CRMClient crmClient;

    /**
     * Construtor para o serviço de clientes.
     *
     * @param objectMapper Mapper para conversão JSON
     * @param clienteMapper Mapper para conversão de Cliente para ClienteCRM
     * @param crmClient Cliente para comunicação com o CRM
     */
    public ClienteService(ObjectMapper objectMapper, ClienteMapper clienteMapper, CRMClient crmClient) {
        this.objectMapper = objectMapper;
        this.clienteMapper = clienteMapper;
        this.crmClient = crmClient;
    }

    /**
     * Processa uma mensagem do Kafka contendo dados de cliente e envia para o CRM.
     *
     * @param mensagem Mensagem JSON recebida do Kafka
     * @return true se o processamento foi bem-sucedido, false caso contrário
     */
    public boolean processarMensagem(String mensagem) {
        try {
            log.info("Processando mensagem do Kafka: {}", mensagem);
            
            // Desserializa a mensagem para um objeto Cliente
            Cliente cliente = objectMapper.readValue(mensagem, Cliente.class);
            log.info("Cliente desserializado com sucesso: {}", cliente.getId());
            
            // Mapeia o cliente para o formato do CRM
            ClienteCRM clienteCRM = clienteMapper.mapToCRM(cliente);
            log.info("Cliente mapeado para formato CRM: {}", clienteCRM.getExternalId());
            
            // Envia o cliente para o CRM
            boolean resultado = crmClient.enviarCliente(clienteCRM);
            
            if (resultado) {
                log.info("Cliente {} processado e enviado com sucesso", cliente.getId());
            } else {
                log.error("Falha ao enviar cliente {} para o CRM", cliente.getId());
            }
            
            return resultado;
            
        } catch (JsonProcessingException e) {
            log.error("Erro ao deserializar mensagem do Kafka: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Erro inesperado ao processar mensagem: {}", e.getMessage(), e);
            return false;
        }
    }
} 