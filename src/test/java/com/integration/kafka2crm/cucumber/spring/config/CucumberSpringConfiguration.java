package com.integration.kafka2crm.cucumber.spring.config;

import com.integration.kafka2crm.Kafka2CrmApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@CucumberContextConfiguration
@SpringBootTest(classes = {Kafka2CrmApplication.class, SpringTestConfig.class})
@EmbeddedKafka(
    partitions = 1,
    topics = {"CLIENTES"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092",
        "auto.create.topics.enable=true",
        "log.dir=target/kafka-logs",
        "log.cleaner.enable=true",
        "log.retention.check.interval.ms=1000",
        "log.retention.hours=1",
        "log.segment.bytes=1073741824",
        "log.cleanup.policy=delete",
        "log.dirs=target/kafka-logs",
        "num.partitions=1",
        "num.recovery.threads.per.data.dir=1",
        "offsets.topic.replication.factor=1",
        "transaction.state.log.replication.factor=1",
        "transaction.state.log.min.isr=1",
        "log.flush.interval.messages=1",
        "log.flush.interval.ms=1000",
        "log.flush.scheduler.interval.ms=1000"
    }
)
@ActiveProfiles("test")
@ContextConfiguration(classes = SpringTestConfig.class)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.group-id=test-group",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.acks=all",
    "spring.kafka.producer.retries=0",
    "spring.kafka.producer.properties[linger.ms]=0",
    "spring.kafka.producer.properties[delivery.timeout.ms]=5000",
    "spring.kafka.producer.properties[request.timeout.ms]=5000",
    "spring.kafka.consumer.properties[auto.offset.reset]=earliest",
    "spring.kafka.consumer.properties[enable.auto.commit]=false",
    "spring.kafka.consumer.properties[group.id]=test-group",
    "spring.kafka.consumer.properties[max.poll.records]=1",
    "spring.kafka.consumer.properties[fetch.min.bytes]=1",
    "spring.kafka.consumer.properties[fetch.max.wait.ms]=100",
    "spring.kafka.consumer.properties[heartbeat.interval.ms]=1000",
    "spring.kafka.consumer.properties[session.timeout.ms]=30000",
    "spring.kafka.consumer.properties[max.poll.interval.ms]=300000"
})
public class CucumberSpringConfiguration {
    static {
        try {
            // Clean up any existing Kafka logs
            Path targetPath = Path.of("target");
            if (Files.exists(targetPath)) {
                Files.walk(targetPath)
                    .filter(path -> path.toString().contains("kafka-logs"))
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            // Ignore deletion errors
                        }
                    });
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
} 