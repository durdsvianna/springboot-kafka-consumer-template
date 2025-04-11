@spring-integration
Feature: Integração Spring com Kafka e CRM

  Scenario: Processamento bem-sucedido de mensagem Kafka
    Given o sistema está configurado com Kafka embarcado
    When envio uma mensagem para o tópico CLIENTES com o cliente
      """
      {
        "id": "123",
        "nome": "João Silva",
        "email": "joao@email.com",
        "endereco": {
          "rua": "Rua A",
          "numero": "123",
          "cidade": "São Paulo"
        }
      }
      """
    Then o cliente deve ser processado e enviado para o CRM

  Scenario: Erro no processamento da mensagem Kafka
    Given o sistema está configurado com Kafka embarcado
    When envio uma mensagem para o tópico CLIENTES com o cliente
      """
      {
        "id": "123",
        "nome": "João Silva",
        "email": "email_invalido"
      }
      """
    Then o sistema deve registrar o erro de processamento 