package com.integration.kafka2crm.cucumber.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.integration.kafka2crm.Kafka2CrmApplication;
import com.integration.kafka2crm.service.ClienteMappingService;
import com.integration.kafka2crm.service.CrmApiService;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration for Cucumber tests.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = Kafka2CrmApplication.class)
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1,
    topics = {"CLIENTES"},
    brokerProperties = {
        "log.dir=target/kafka-logs",
        "auto.create.topics.enable=true",
        "log.retention.ms=3600000",
        "offsets.topic.replication.factor=1",
        "transaction.state.log.replication.factor=1",
        "transaction.state.log.min.isr=1"
    }
)
public class CucumberTestConfig {

    private static final Logger log = LoggerFactory.getLogger(CucumberTestConfig.class);
    
    @Value("${kafka.topic.clientes}")
    private String topicName;
    
    @Value("${crm.api.url}")
    private String crmApiUrl;

    // Usar MockBean em vez de Bean + mock manualmente
    @MockBean
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void configureRestAssured() {
        log.info("Configurando RestAssured para testes de integração com URL: {}", crmApiUrl);
        
        // Limpar diretórios Kafka antes de iniciar
        cleanKafkaLogs();
        
        RestAssured.baseURI = crmApiUrl;
        RestAssured.config = RestAssuredConfig.config()
            .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }
    
    @PreDestroy
    public void cleanup() {
        log.info("Limpando recursos após os testes");
        cleanKafkaLogs();
    }
    
    @Bean
    public NewTopic clientesTopic() {
        return TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    private void cleanKafkaLogs() {
        File kafkaLogDir = new File("target/kafka-logs");
        if (kafkaLogDir.exists()) {
            log.info("Cleaning up Kafka logs directory before tests");
            File[] files = kafkaLogDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean deleted = deleteRecursively(file);
                    if (!deleted) {
                        log.warn("Could not delete Kafka log file: {}", file.getAbsolutePath());
                    }
                }
            }
        }
    }
    
    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        boolean success = true;
        try {
            success = file.delete();
        } catch (Exception e) {
            log.warn("Erro ao excluir arquivo: {}", e.getMessage());
            success = false;
        }
        return success;
    }

    /**
     * Creates an ObjectMapper for tests.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    
    /**
     * Creates a ClienteMappingService for tests.
     */
    @Bean
    @Primary
    public ClienteMappingService clienteMappingService() {
        return new ClienteMappingService();
    }
    
    /**
     * Creates a CrmApiService for tests.
     */
    @Bean
    @Primary
    public CrmApiService crmApiService() {
        // Usar a instância do MockBean
        return new CrmApiService(restTemplate);
    }
    
    /**
     * Configure the Kafka producer factory
     */
    @Bean
    @Primary
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5000);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * Configure the KafkaTemplate using our producer factory
     */
    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * Configure the Kafka consumer factory
     */
    @Bean
    @Primary
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka2crm-test-group-" + System.currentTimeMillis());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * Configure the Kafka listener container factory
     */
    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
} 