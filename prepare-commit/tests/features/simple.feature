# language: en
Feature: Simple Kafka to CRM Integration

  Scenario: Mock Kafka and CRM communication
    Given the Kafka consumer is configured
    When a client message is processed
    Then the client data is sent to the CRM API 