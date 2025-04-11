package com.integration.kafka2crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests that the Spring context loads correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"CLIENTES"})
public class SpringContextTest {

    @Test
    public void contextLoads() {
        // This test will fail if the Spring context cannot be created
    }
} 