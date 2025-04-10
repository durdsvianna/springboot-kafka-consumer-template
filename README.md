# Spring Boot Kafka Consumer Template

Template de microserviço Spring Boot com integração entre Kafka e CRM.

## Funcionalidades

- Consumo de mensagens do Kafka
- Conversão de dados para formato do CRM
- Integração com API REST de CRM
- Tratamento de erros

## Requisitos

- Java 21
- Spring Boot 3.2.3
- Apache Kafka

## Configuração

O arquivo `application.properties` contém as configurações básicas para:

- Conexão com o Kafka
- Configuração do servidor
- Comunicação com API do CRM

## Testes

O projeto inclui testes unitários e de integração que utilizam:

- JUnit 5
- Cucumber
- WireMock
- Kafka Embarcado

## Executando o projeto

```bash
mvn clean install
mvn spring-boot:run
```

## Executando os testes

```bash
mvn clean test
``` 