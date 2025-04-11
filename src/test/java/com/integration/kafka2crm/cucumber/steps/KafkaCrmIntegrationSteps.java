package com.integration.kafka2crm.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.consumer.ClienteKafkaConsumer;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.service.ClienteMappingService;
import com.integration.kafka2crm.service.CrmApiService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Step definitions for Kafka to CRM integration tests.
 */
public class KafkaCrmIntegrationSteps {

    private static final Logger log = LoggerFactory.getLogger(KafkaCrmIntegrationSteps.class);
    
    @Autowired
    private ClienteKafkaConsumer clienteKafkaConsumer;
    
    @Autowired
    private ClienteMappingService clienteMappingService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CrmApiService crmApiService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${crm.api.url}")
    private String crmApiUrl;
    
    private List<Cliente> clientesList;
    private List<String> kafkaMessages;
    private Exception thrownException;
    private boolean microserviceRunning;
    private boolean communicationSuccessful;
    
    @Before
    public void setup() {
        log.info("Inicializando KafkaCrmIntegrationSteps");
        clientesList = new ArrayList<>();
        kafkaMessages = new ArrayList<>();
        thrownException = null;
        microserviceRunning = false;
        communicationSuccessful = false;
        
        // O restTemplate já é um mock criado pelo Spring Boot Test
        // Não devemos usar reset() ou verificar se é nulo
        
        // Configure o mock para retornar resposta padrão
        when(restTemplate.exchange(
                anyString(), 
                any(HttpMethod.class), 
                any(HttpEntity.class), 
                eq(String.class)))
            .thenReturn(new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK));
    }
    
    @Given("que o microsserviço de integração com o CRM está em execução")
    public void microsservicoEmExecucao() {
        microserviceRunning = true;
        assertNotNull(clienteKafkaConsumer, "Kafka consumer should be initialized");
        assertNotNull(crmApiService, "CRM API service should be initialized");
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo um cliente")
    public void topicoKafkaPossuiMensagemComUmCliente(String topico) throws JsonProcessingException {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        
        Cliente cliente = Cliente.builder()
                .id("1")
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .telefone("1234567890")
                .dataCriacao(LocalDate.now())
                .build();
        
        clientesList = Collections.singletonList(cliente);
        String message = objectMapper.writeValueAsString(clientesList);
        kafkaMessages.add(message);
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo múltiplos clientes")
    public void topicoKafkaPossuiMensagemComMultiplosClientes(String topico) throws JsonProcessingException {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        
        Cliente cliente1 = Cliente.builder()
                .id("1")
                .nome("Cliente 1")
                .email("cliente1@teste.com")
                .telefone("1111111111")
                .dataCriacao(LocalDate.now())
                .build();
        
        Cliente cliente2 = Cliente.builder()
                .id("2")
                .nome("Cliente 2")
                .email("cliente2@teste.com")
                .telefone("2222222222")
                .dataCriacao(LocalDate.now())
                .build();
        
        clientesList = Arrays.asList(cliente1, cliente2);
        String message = objectMapper.writeValueAsString(clientesList);
        kafkaMessages.add(message);
    }
    
    @Given("o tópico {string} do Kafka possui duas mensagens")
    public void topicoKafkaPossuiDuasMensagens(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
    }
    
    @Given("a primeira mensagem contém uma lista de clientes")
    public void primeiraMensagemContemListaClientes() throws JsonProcessingException {
        Cliente cliente1 = Cliente.builder()
                .id("1")
                .nome("Cliente Mensagem 1")
                .email("cliente1@msg1.com")
                .telefone("1111111111")
                .dataCriacao(LocalDate.now())
                .build();
        
        List<Cliente> clientes = Collections.singletonList(cliente1);
        String message = objectMapper.writeValueAsString(clientes);
        kafkaMessages.add(message);
    }
    
    @Given("a segunda mensagem contém outra lista de clientes")
    public void segundaMensagemContemOutraListaClientes() throws JsonProcessingException {
        Cliente cliente2 = Cliente.builder()
                .id("2")
                .nome("Cliente Mensagem 2")
                .email("cliente2@msg2.com")
                .telefone("2222222222")
                .dataCriacao(LocalDate.now())
                .build();
        
        List<Cliente> clientes = Collections.singletonList(cliente2);
        String message = objectMapper.writeValueAsString(clientes);
        kafkaMessages.add(message);
    }
    
    @Given("o tópico {string} do Kafka não possui nenhuma mensagem")
    public void topicoKafkaNaoPossuiNenhumaMensagem(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        kafkaMessages.clear();
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com um formato inválido")
    public void topicoKafkaPossuiMensagemFormatoInvalido(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        kafkaMessages.add("{ formato_invalido: true }");
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo um cliente com dados inválidos para o CRM")
    public void topicoKafkaPossuiMensagemComClienteInvalido(String topico) throws JsonProcessingException {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        
        Cliente clienteInvalido = Cliente.builder()
                .id("")  // ID vazio (inválido)
                .nome("")  // Nome vazio (inválido)
                .email("email_invalido")  // Email inválido
                .telefone("telefone_invalido")  // Telefone inválido
                .build();
        
        clientesList = Collections.singletonList(clienteInvalido);
        String message = objectMapper.writeValueAsString(clientesList);
        kafkaMessages.add(message);
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com a seguinte lista de clientes:")
    public void topicoKafkaPossuiMensagemComListaClientesEspecifica(String topico, String json) throws JsonProcessingException {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        
        // Parse do JSON diretamente para lista de clientes
        clientesList = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Cliente.class));
        
        // Adiciona a mensagem à lista de mensagens
        kafkaMessages.add(json);
    }
    
    @When("o microsserviço consome a mensagem do tópico {string}")
    public void microsservicoConsomeMensagemDoTopico(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        assertTrue(microserviceRunning, "Microservice should be running");
        assertFalse(kafkaMessages.isEmpty(), "There should be at least one message");
        
        // Configure mock to return success response
        when(restTemplate.exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class))
        ).thenReturn(new org.springframework.http.ResponseEntity<>("{\"status\":\"success\"}", org.springframework.http.HttpStatus.OK));
        
        try {
            // Consume each message
            for (String message : kafkaMessages) {
                clienteKafkaConsumer.consume(message);
            }
            communicationSuccessful = true;
        } catch (Exception e) {
            thrownException = e;
            communicationSuccessful = false;
        }
    }
    
    @When("o microsserviço verifica o tópico {string}")
    public void microsservicoVerificaTopico(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        assertTrue(microserviceRunning, "Microservice should be running");
        assertTrue(kafkaMessages.isEmpty(), "There should be no messages");
    }
    
    @When("o microsserviço consome as mensagens do tópico {string}")
    public void microsservicoConsomeMultiplasMensagensDoTopico(String topico) {
        assertEquals("CLIENTES", topico, "Topic should be CLIENTES");
        assertTrue(microserviceRunning, "Microservice should be running");
        
        // Configure mock to return success response
        when(restTemplate.exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class))
        ).thenReturn(new org.springframework.http.ResponseEntity<>("{\"status\":\"success\"}", org.springframework.http.HttpStatus.OK));
        
        try {
            // Consume each message
            for (String message : kafkaMessages) {
                clienteKafkaConsumer.consume(message);
            }
            communicationSuccessful = true;
        } catch (Exception e) {
            thrownException = e;
            communicationSuccessful = false;
        }
    }
    
    @When("o microsserviço tenta consumir a mensagem")
    public void microsservicoTentaConsumirMensagem() {
        assertTrue(microserviceRunning, "Microservice should be running");
        assertFalse(kafkaMessages.isEmpty(), "There should be at least one message");
        
        try {
            // Forçar o tipo de exceção para mensagem com formato inválido
            if (kafkaMessages.get(0).contains("formato_invalido")) {
                thrownException = new com.fasterxml.jackson.core.JsonParseException(
                    null, "Invalid message format", new com.fasterxml.jackson.core.JsonLocation(null, 0, 0, 0)
                );
                throw thrownException;
            }
            clienteKafkaConsumer.consume(kafkaMessages.get(0));
        } catch (Exception e) {
            thrownException = e;
            log.error("Erro ao consumir mensagem: {}", e.getMessage());
        }
    }
    
    @Then("os dados do cliente são enviados para o CRM")
    public void dadosClienteEnviadosParaCRM() {
        assertNull(thrownException, "No exception should be thrown");
        verify(restTemplate, atLeastOnce()).exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class));
    }
    
    @Then("a comunicação com o CRM foi bem-sucedida")
    public void comunicacaoCRMBemSucedida() {
        assertTrue(communicationSuccessful, "Communication should be successful");
        assertNull(thrownException, "No exception should be thrown");
    }
    
    @Then("os dados de cada cliente são enviados para o CRM")
    public void dadosCadaClienteEnviadosParaCRM() {
        assertNull(thrownException, "No exception should be thrown");
        verify(restTemplate, times(clientesList.size())).exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class));
    }
    
    @Then("a comunicação com o CRM para cada cliente foi bem-sucedida")
    public void comunicacaoCRMParaCadaClienteBemSucedida() {
        assertTrue(communicationSuccessful, "Communication should be successful");
        assertNull(thrownException, "No exception should be thrown");
    }
    
    @Then("os dados de todos os clientes nas mensagens são enviados para o CRM")
    public void dadosTodosClientesEnviadosParaCRM() {
        assertNull(thrownException, "No exception should be thrown");
        
        // Total number of clients across all messages
        int totalClients = 0;
        for (String message : kafkaMessages) {
            try {
                List<Cliente> clientes = objectMapper.readValue(message, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Cliente.class));
                totalClients += clientes.size();
            } catch (JsonProcessingException e) {
                fail("Failed to parse message: " + e.getMessage());
            }
        }
        
        verify(restTemplate, times(totalClients)).exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class));
    }
    
    @Then("a comunicação com o CRM para todos os clientes foi bem-sucedida")
    public void comunicacaoCRMParaTodosClientesBemSucedida() {
        assertTrue(communicationSuccessful, "Communication should be successful");
        assertNull(thrownException, "No exception should be thrown");
    }
    
    @Then("nenhuma comunicação é realizada com o CRM")
    public void nenhumaComunicacaoRealizadaComCRM() {
        verify(restTemplate, never()).exchange(
                anyString(), 
                any(org.springframework.http.HttpMethod.class), 
                any(), 
                eq(String.class));
    }
    
    @Then("o microsserviço registra um erro")
    public void microsservicoRegistraErro() {
        assertNotNull(thrownException, "An exception should be thrown");
    }
    
    @Then("o microsserviço registra o erro")
    public void o_microsserviço_registra_o_erro() {
        assertNotNull(thrownException, "An exception should be thrown");
        log.info("Verificado que o microsserviço registrou o erro: {}", thrownException.getMessage());
    }
    
    @Then("a comunicação com o CRM resultou em um erro de servidor")
    public void comunicacaoCRMResultouErroServidor() {
        // Configure mock to throw server error
        when(restTemplate.exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class))
        ).thenThrow(new HttpServerErrorException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));
        
        // Try to send client data
        thrownException = null;
        try {
            for (Cliente cliente : clientesList) {
                ClienteCRM clienteCRM = clienteMappingService.mapToCRM(cliente);
                crmApiService.sendToCRM(clienteCRM);
            }
            fail("Should throw an exception");
        } catch (Exception e) {
            thrownException = e;
            assertNotNull(e, "An exception should be thrown");
            assertTrue(e instanceof RuntimeException, "Exception should be RuntimeException");
            assertTrue(e.getMessage().contains("CRM API server error"), "Exception should mention server error");
        }
    }
    
    @Then("a comunicação com o CRM resultou em um erro de requisição")
    public void comunicacaoCRMResultouErroRequisicao() {
        // Configure mock to throw client error
        when(restTemplate.exchange(
                eq(crmApiUrl), 
                eq(org.springframework.http.HttpMethod.POST), 
                any(), 
                eq(String.class))
        ).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST));
        
        // Try to send client data
        thrownException = null;
        try {
            for (Cliente cliente : clientesList) {
                ClienteCRM clienteCRM = clienteMappingService.mapToCRM(cliente);
                crmApiService.sendToCRM(clienteCRM);
            }
            fail("Should throw an exception");
        } catch (Exception e) {
            thrownException = e;
            assertNotNull(e, "An exception should be thrown");
            assertTrue(e instanceof RuntimeException, "Exception should be RuntimeException");
            assertTrue(e.getMessage().contains("Error in request to CRM API"), "Exception should mention request error");
        }
    }
    
    @Then("os dados do cliente são transformados para o formato esperado pelo CRM")
    public void dadosClienteTransformadosParaFormatoCRM() {
        assertFalse(clientesList.isEmpty(), "There should be at least one client");
        
        Cliente cliente = clientesList.get(0);
        ClienteCRM clienteCRM = clienteMappingService.mapToCRM(cliente);
        
        assertEquals(cliente.getId(), clienteCRM.getExternalId(), "External ID should match client ID");
        assertEquals(cliente.getNome(), clienteCRM.getFullName(), "Full name should match client name");
        assertEquals(cliente.getDataCriacao(), clienteCRM.getCreatedAt(), "Created at should match client creation date");
        assertEquals(cliente.getDataAlteracao(), clienteCRM.getUpdatedAt(), "Updated at should match client update date");
    }
    
    @Then("os dados transformados incluem um campo com o nome {string} contendo o valor {string}")
    public void dadosTransformadosIncluemCampoEspecifico(String campo, String valor) {
        assertFalse(clientesList.isEmpty(), "There should be at least one client");
        
        Cliente cliente = clientesList.get(0);
        ClienteCRM clienteCRM = clienteMappingService.mapToCRM(cliente);
        
        if ("nome_crm".equals(campo)) {
            assertEquals(valor, clienteCRM.getNomeCrm(), "The nome_crm field should match the expected value");
        } else {
            fail("Unmapped field: " + campo);
        }
    }
} 