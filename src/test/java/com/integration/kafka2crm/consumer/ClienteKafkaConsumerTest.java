package com.integration.kafka2crm.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.service.CrmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClienteConsumerTest {

    @Mock
    private CrmService crmService;

    private ObjectMapper objectMapper;
    private ClienteConsumer consumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        consumer = new ClienteConsumer(objectMapper, crmService);
    }

    @Test
    void shouldProcessMessageAndSendToCrm() throws Exception {
        // Given
        Cliente cliente = Cliente.builder()
                .id("1")
                .nome("João")
                .email("joao@email.com")
                .telefone("1234567890")
                .build();

        String message = objectMapper.writeValueAsString(Collections.singletonList(cliente));

        // When
        consumer.consume(message);

        // Then
        verify(crmService).sendToCrm(any(List.class));
    }
} 