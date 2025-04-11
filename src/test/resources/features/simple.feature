# language: en
Feature: Simple Kafka to CRM Integration

  Scenario: Successful client data integration
    Given the CRM API is available
    When I send a message to Kafka with the following clients:
      | id | nome  | email           | telefone    |
      | 1  | João  | joao@email.com  | 1234567890  |
    Then the CRM should receive the following clients:
      | id | name  | email           | phone       |
      | 1  | João  | joao@email.com  | 1234567890  |

  Scenario: Handle CRM server error
    Given the CRM API returns a server error
    When I send a message to Kafka with the following clients:
      | id | nome  | email           | telefone    |
      | 1  | João  | joao@email.com  | 1234567890  |
    Then the error should be logged

  Scenario: Handle CRM client error
    Given the CRM API returns a client error
    When I send a message to Kafka with the following clients:
      | id | nome  | email           | telefone    |
      | 1  | João  | joao@email.com  | 1234567890  |
    Then the error should be logged 