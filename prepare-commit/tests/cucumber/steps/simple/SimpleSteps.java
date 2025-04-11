package com.integration.kafka2crm.cucumber.steps.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.integration.kafka2crm.client.CrmApiClient;
import com.integration.kafka2crm.consumer.ClienteKafkaConsumer;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.model.Endereco;
import com.integration.kafka2crm.model.Produto;
import com.integration.kafka2crm.service.ClienteMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleSteps {

    private ObjectMapper objectMapper;
    private ClienteMapper clienteMapper;
    
    @Mock
    private CrmApiClient crmApiClient;
    
    private ClienteKafkaConsumer clienteKafkaConsumer;
    private List<Cliente> clients;
    
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Set up ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Set up ClienteMapper
        clienteMapper = new ClienteMapper();
        
        // Set up mock for CrmApiClient
        when(crmApiClient.sendClientToCrm(any(ClienteCRM.class))).thenReturn(true);
        
        // Set up Kafka consumer
        clienteKafkaConsumer = new ClienteKafkaConsumer(objectMapper, clienteMapper, crmApiClient);
        
        // Create a test client
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
    
    @Given("the Kafka consumer is configured")
    public void theKafkaConsumerIsConfigured() {
        // The consumer is already configured in the setup method
    }
    
    @When("a client message is processed")
    public void aClientMessageIsProcessed() throws Exception {
        // Convert client to JSON and process it
        String message = objectMapper.writeValueAsString(clients);
        clienteKafkaConsumer.consumeClienteMessage(message);
    }
    
    @Then("the client data is sent to the CRM API")
    public void theClientDataIsSentToTheCrmApi() {
        // Verify that sendClientToCrm was called with any ClienteCRM object
        verify(crmApiClient).sendClientToCrm(any(ClienteCRM.class));
    }
} 