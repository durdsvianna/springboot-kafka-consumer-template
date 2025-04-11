package com.integration.kafka2crm.cucumber.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class SpringIntegrationSteps {

    private static final Logger log = LoggerFactory.getLogger(SpringIntegrationSteps.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<ClienteCRM> clientesEnviados = new ArrayList<>();

    @Given("o sistema está configurado com Kafka embarcado")
    public void sistemaConfiguradoComKafka() {
        log.info("Kafka embarcado configurado em: {}", embeddedKafka.getBrokersAsString());
        mockServer = MockRestServiceServer.createServer(restTemplate);
        
        // Configurar o mock do CRM para aceitar qualquer requisição
        mockServer.expect(requestTo("http://localhost:8080/api/v2/clientes"))
                .andRespond(withSuccess());
    }

    @When("envio uma mensagem para o tópico CLIENTES com o cliente")
    public void envioMensagemParaKafka(String clienteJson) throws Exception {
        log.info("Enviando mensagem para Kafka: {}", clienteJson);
        kafkaTemplate.send("CLIENTES", clienteJson).get(5, TimeUnit.SECONDS);
    }

    @Then("o cliente deve ser processado e enviado para o CRM")
    public void clienteDeveSerEnviadoParaCRM() throws Exception {
        // Aguardar processamento
        Thread.sleep(2000);

        // Verificar se o mock foi chamado
        mockServer.verify();
    }

    @Then("o sistema deve registrar o erro de processamento")
    public void sistemaDeveRegistrarErro() {
        // Implementar verificação de logs ou status de erro
        log.info("Verificando registro de erro no sistema");
    }
} 