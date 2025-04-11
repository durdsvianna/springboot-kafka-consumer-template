package com.integration.kafka2crm.service;

import com.integration.kafka2crm.client.CrmApiClient;
import com.integration.kafka2crm.mapper.ClienteMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço para processamento de clientes e integração com o CRM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteMapper clienteMapper;
    private final CrmApiClient crmApiClient;

    /**
     * Processa um cliente e envia para o CRM
     *
     * @param cliente Cliente a ser processado
     * @return true se o processamento for bem-sucedido, false caso contrário
     */
    public boolean processarCliente(Cliente cliente) {
        try {
            log.info("Processando cliente ID: {}", cliente.getId());
            
            // Validação básica
            if (cliente.getId() == null || cliente.getNome() == null) {
                log.error("Cliente com dados incompletos: {}", cliente);
                return false;
            }
            
            // Mapeia para o formato do CRM
            ClienteCRM clienteCRM = clienteMapper.toClienteCRM(cliente);
            log.debug("Cliente mapeado para formato CRM: {}", clienteCRM);
            
            // Envia para o CRM
            crmApiClient.sendClientToCrm(clienteCRM);
            log.info("Cliente ID: {} enviado com sucesso para o CRM", cliente.getId());
            
            return true;
        } catch (Exception e) {
            log.error("Erro ao processar cliente ID: {}", cliente.getId(), e);
            return false;
        }
    }
} 