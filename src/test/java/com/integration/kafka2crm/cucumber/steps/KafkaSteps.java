package com.integration.kafka2crm.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaSteps {

    private static final Logger log = LoggerFactory.getLogger(KafkaSteps.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${kafka.topic.clientes}")
    private String topicName;
    
    private List<CountDownLatch> latches = new ArrayList<>();
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo um cliente")
    public void sendSingleClientToKafka(String topic) throws JsonProcessingException, InterruptedException {
        List<Cliente> clients = new ArrayList<>();
        clients.add(createTestClient("1", "Cliente Teste"));
        
        sendClientsToKafka(topic, clients);
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo múltiplos clientes")
    public void sendMultipleClientsToKafka(String topic) throws JsonProcessingException, InterruptedException {
        List<Cliente> clients = new ArrayList<>();
        clients.add(createTestClient("1", "Cliente Teste 1"));
        clients.add(createTestClient("2", "Cliente Teste 2"));
        clients.add(createTestClient("3", "Cliente Teste 3"));
        
        sendClientsToKafka(topic, clients);
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com uma lista contendo um cliente com dados inválidos para o CRM")
    public void sendInvalidClientToKafka(String topic) throws JsonProcessingException, InterruptedException {
        List<Cliente> clients = new ArrayList<>();
        // Create a client with invalid data (e.g., null name)
        Cliente invalidClient = new Cliente();
        invalidClient.setId("999");
        invalidClient.setNome(null); // Invalid: name is null
        invalidClient.setDataCriacao(LocalDate.now());
        invalidClient.setDataAlteracao(LocalDate.now());
        
        clients.add(invalidClient);
        
        sendClientsToKafka(topic, clients);
    }
    
    @Given("o tópico {string} do Kafka possui duas mensagens")
    public void kafkaTopicHasTwoMessages(String topic) {
        log.info("Setting up two messages in Kafka topic: {}", topic);
    }
    
    @Given("a primeira mensagem contém uma lista de clientes")
    public void firstMessageContainsClientList() throws JsonProcessingException, InterruptedException {
        List<Cliente> clients = new ArrayList<>();
        clients.add(createTestClient("101", "Cliente da Mensagem 1 - A"));
        clients.add(createTestClient("102", "Cliente da Mensagem 1 - B"));
        
        sendClientsToKafka(topicName, clients);
    }
    
    @Given("a segunda mensagem contém outra lista de clientes")
    public void secondMessageContainsAnotherClientList() throws JsonProcessingException, InterruptedException {
        List<Cliente> clients = new ArrayList<>();
        clients.add(createTestClient("201", "Cliente da Mensagem 2 - A"));
        clients.add(createTestClient("202", "Cliente da Mensagem 2 - B"));
        
        sendClientsToKafka(topicName, clients);
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com um formato inválido")
    public void topicHasInvalidFormatMessage(String topic) throws InterruptedException {
        String invalidJson = "{this is not valid json}";
        log.info("Sending invalid format message to Kafka topic {}: {}", topic, invalidJson);
        
        CountDownLatch latch = new CountDownLatch(1);
        kafkaTemplate.send(topic, invalidJson).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Invalid message sent successfully to topic: {}", topic);
            } else {
                log.error("Failed to send invalid message to topic {}: {}", topic, ex.getMessage());
            }
            latch.countDown();
        });
        
        boolean delivered = latch.await(5, TimeUnit.SECONDS);
        if (!delivered) {
            log.warn("Timeout waiting for invalid message delivery confirmation");
        }
    }
    
    @Given("o tópico {string} do Kafka não possui nenhuma mensagem")
    public void topicHasNoMessages(String topic) {
        log.info("Ensuring topic {} has no messages", topic);
        // No action needed, as we're just asserting the absence of messages
    }
    
    @Given("o tópico {string} do Kafka possui uma mensagem com a seguinte lista de clientes:")
    public void topicHasSpecificClientList(String topic, String clientsJson) throws InterruptedException {
        log.info("Sending specific client JSON to Kafka topic {}: {}", topic, clientsJson);
        
        CountDownLatch latch = new CountDownLatch(1);
        kafkaTemplate.send(topic, clientsJson).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Specific JSON message sent successfully to topic: {}", topic);
            } else {
                log.error("Failed to send specific JSON message to topic {}: {}", topic, ex.getMessage());
            }
            latch.countDown();
        });
        
        boolean delivered = latch.await(5, TimeUnit.SECONDS);
        if (!delivered) {
            log.warn("Timeout waiting for specific JSON message delivery confirmation");
        }
    }
    
    @When("o microsserviço consome a mensagem do tópico {string}")
    public void microserviceConsumesMessage(String topic) throws InterruptedException {
        log.info("Waiting for the microservice to consume message from topic: {}", topic);
        // Give some time for the consumer to process the message
        TimeUnit.SECONDS.sleep(1);
    }
    
    @When("o microsserviço consome as mensagens do tópico {string}")
    public void microserviceConsumesMultipleMessages(String topic) throws InterruptedException {
        log.info("Waiting for the microservice to consume messages from topic: {}", topic);
        // Give some time for the consumer to process the messages
        TimeUnit.SECONDS.sleep(2);
    }
    
    @When("o microsserviço verifica o tópico {string}")
    public void microserviceChecksTopicForMessages(String topic) throws InterruptedException {
        log.info("Microservice is checking topic: {}", topic);
        // Give some time for the consumer to check the topic
        TimeUnit.SECONDS.sleep(1);
    }
    
    @When("o microsserviço tenta consumir a mensagem")
    public void microserviceAttemptsToConsumeMessage() throws InterruptedException {
        log.info("Waiting for the microservice to attempt to consume the message");
        // Give some time for the consumer to attempt to process the message
        TimeUnit.SECONDS.sleep(1);
    }
    
    private Cliente createTestClient(String id, String nome) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome(nome);
        cliente.setDataCriacao(LocalDate.now());
        cliente.setDataAlteracao(LocalDate.now());
        return cliente;
    }
    
    private void sendClientsToKafka(String topic, List<Cliente> clients) throws JsonProcessingException, InterruptedException {
        String json = objectMapper.writeValueAsString(clients);
        log.info("Sending to Kafka topic {}: {}", topic, json);
        
        CountDownLatch latch = new CountDownLatch(1);
        kafkaTemplate.send(topic, json).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to topic: {} with offset {}", 
                    topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic {}: {}", topic, ex.getMessage());
            }
            latch.countDown();
        });
        
        boolean delivered = latch.await(5, TimeUnit.SECONDS);
        if (!delivered) {
            log.warn("Timeout waiting for message delivery confirmation");
        }
        
        // Store the latch for cleanup
        latches.add(latch);
    }
} 