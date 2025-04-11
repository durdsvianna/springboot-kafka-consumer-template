# language: en
Feature: Integração de Clientes do Kafka com CRM

Scenario: Processamento bem-sucedido de uma mensagem com um cliente do Kafka
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com uma lista contendo um cliente
  When o microsserviço consome a mensagem do tópico "CLIENTES"
  Then os dados do cliente são enviados para o CRM
  And a comunicação com o CRM foi bem-sucedida

Scenario: Processamento bem-sucedido de uma mensagem com múltiplos clientes do Kafka
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com uma lista contendo múltiplos clientes
  When o microsserviço consome a mensagem do tópico "CLIENTES"
  Then os dados de cada cliente são enviados para o CRM
  And a comunicação com o CRM para cada cliente foi bem-sucedida

Scenario: Processamento bem-sucedido de múltiplas mensagens do Kafka
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui duas mensagens
  And a primeira mensagem contém uma lista de clientes
  And a segunda mensagem contém outra lista de clientes
  When o microsserviço consome as mensagens do tópico "CLIENTES"
  Then os dados de todos os clientes nas mensagens são enviados para o CRM
  And a comunicação com o CRM para todos os clientes foi bem-sucedida

Scenario: Nenhum cliente para processar no tópico Kafka
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka não possui nenhuma mensagem
  When o microsserviço verifica o tópico "CLIENTES"
  Then nenhuma comunicação é realizada com o CRM

Scenario: Erro ao consumir mensagem do tópico Kafka
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com um formato inválido
  When o microsserviço tenta consumir a mensagem
  Then o microsserviço registra um erro
  And nenhuma comunicação é realizada com o CRM

Scenario: Erro na comunicação com o CRM (erro no servidor)
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com uma lista contendo um cliente
  When o microsserviço consome a mensagem do tópico "CLIENTES"
  Then os dados do cliente são enviados para o CRM
  And a comunicação com o CRM resultou em um erro de servidor
  And o microsserviço registra o erro

Scenario: Erro na comunicação com o CRM (erro na requisição)
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com uma lista contendo um cliente com dados inválidos para o CRM
  When o microsserviço consome a mensagem do tópico "CLIENTES"
  Then os dados do cliente são enviados para o CRM
  And a comunicação com o CRM resultou em um erro de requisição
  And o microsserviço registra o erro

Scenario: Mapeamento dos dados do cliente para a entidade do CRM
  Given que o microsserviço de integração com o CRM está em execução
  And o tópico "CLIENTES" do Kafka possui uma mensagem com a seguinte lista de clientes:
    """
    [
      {
        "id": "555",
        "nome": "Cliente Teste Mapeamento",
        "data_criacao": "2025-04-03",
        "data_alteracao": "2025-04-03"
      }
    ]
    """
  When o microsserviço consome a mensagem do tópico "CLIENTES"
  Then os dados do cliente são transformados para o formato esperado pelo CRM
  And os dados transformados incluem um campo com o nome "nome_crm" contendo o valor "Cliente Teste Mapeamento"
  And a comunicação com o CRM foi bem-sucedida 