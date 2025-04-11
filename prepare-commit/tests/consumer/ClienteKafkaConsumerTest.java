package com.integration.kafka2crm.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.integration.kafka2crm.client.CrmApiClient;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.model.Endereco;
import com.integration.kafka2crm.model.Produto;
import com.integration.kafka2crm.service.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteKafkaConsumerTest {

    private ObjectMapper objectMapper;
    
    @Mock
    private CrmApiClient crmApiClient;
    
    private ClienteMapper clienteMapper;
    private ClienteKafkaConsumer consumer;
    private List<Cliente> clients;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        clienteMapper = new ClienteMapper();
        
        when(crmApiClient.sendClientToCrm(any(ClienteCRM.class))).thenReturn(true);
        
        consumer = new ClienteKafkaConsumer(objectMapper, clienteMapper, crmApiClient);
        
        Cliente client = Cliente.builder()
                .id("123")
                .nome("John Smith")
                .email("john.smith@example.com")
                .cpf("12345678900")
                .dataNascimento(LocalDate.of(1985, 5, 15))
                .telefone("11999998888")
                .dataCriacao(LocalDate.now())
                .dataAlteracao(LocalDate.now())
                .endereco(Endereco.builder()
                        .logradouro("Main Street")
                        .numero("123")
                        .complemento("Apt 45")
                        .bairro("Downtown")
                        .cidade("New York")
                        .estado("NY")
                        .cep("10001")
                        .build())
                .produtos(Arrays.asList(
                        Produto.builder()
                                .codigo("P001")
                                .nome("Product 1")
                                .descricao("Product 1 Description")
                                .valor(new BigDecimal("99.90"))
                                .categoria("Electronics")
                                .build()
                ))
                .build();
        
        clients = Arrays.asList(client);
    }

    @Test
    void shouldProcessClientMessageAndSendToCrm() throws Exception {
        // Given a message with a list of clients
        String message = objectMapper.writeValueAsString(clients);
        
        // When the consumer processes the message
        consumer.consumeClienteMessage(message);
        
        // Then the client data should be sent to CRM
        verify(crmApiClient, times(1)).sendClientToCrm(any(ClienteCRM.class));
    }

    @Test
    void shouldHandleInvalidJsonMessage() {
        // Given an invalid JSON message
        String invalidMessage = "{invalid-json}";
        
        // When the consumer processes the message
        consumer.consumeClienteMessage(invalidMessage);
        
        // Then no client data should be sent to CRM
        verify(crmApiClient, times(0)).sendClientToCrm(any(ClienteCRM.class));
    }
} 