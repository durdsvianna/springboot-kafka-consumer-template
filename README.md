# Springboot Kafka to CRM Integration Service

## Project Overview

This project is a Spring Boot microservice that integrates Kafka with a CRM system. It consumes messages from a Kafka topic, processes client data, and sends it to a CRM API. The service is built with Spring Boot 3.2.3 and Java 21.

## Architecture

The microservice follows a clean architecture approach:

1. **Data Models**:
   - Domain models (`Cliente`, `Endereco`, `Produto`)
   - CRM-specific models (`ClienteCRM`, `EnderecoCRM`, `ProdutoCRM`)

2. **Configuration**:
   - `KafkaConfig` for Kafka consumer configuration
   - `WebClientConfig` for REST API communication

3. **Core Logic**:
   - `ClienteMapper` for transforming between domain models
   - `ClienteService` for processing clients and CRM integration
   - `ClienteConsumer` for Kafka message consumption

4. **External Communication**:
   - `CrmApiClient` for interacting with the CRM REST API

## Testing Strategy

### Testing Challenges and Solutions

While developing the testing framework for this project, we encountered several challenges with integration testing, particularly with Cucumber and Spring Boot. We implemented an optimized strategy that balances simplicity and effectiveness.

#### Key Testing Components

1. **Simple Unit Tests**:
   - `SimpleUnitTest` - Basic test without Spring Boot dependencies
   - `SimpleNoKafkaTest` - Focused test isolated from infrastructure

2. **Cucumber Integration with Standalone Configuration**:
   - `SimpleTest` and `CucumberJUnit4Runner` - Cucumber test runners
   - `StandaloneSteps` - Step definitions independent of real components
   - `CucumberStandaloneConfig` - Minimal Spring configuration

3. **Mock Infrastructure**:
   - Avoids direct Kafka dependencies in test environment
   - Uses simplified mocks instead of full infrastructure components

### Current Testing Architecture

Our testing strategy uses the following approach:

1. **Cucumber Test Framework**:
   - `simple.feature` - BDD scenarios in Gherkin language
   - Test runner configurations with organized output paths
   - Standalone steps that simulate behavior without real dependencies

2. **Minimal Spring Integration**:
   - Configures only what's needed for tests
   - Prevents complex bean initialization issues
   - Avoids heavyweight Spring context for faster tests

3. **Clear Logging and Reporting**:
   - Detailed test execution logs
   - Scenario start/end markers
   - Success/failure status logging

### Benefits of the Current Approach

1. **Simplicity**: Tests are straightforward to understand and maintain
2. **Reliability**: Fewer moving parts means fewer potential points of failure
3. **Performance**: Tests run faster with minimal context loading
4. **Isolation**: Each test properly initializes and cleans up its environment
5. **Readability**: BDD scenarios describe behavior in natural language

## Running Tests

To run the unit and integration tests:

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=SimpleTest

# Run multiple test classes
mvn clean test -Dtest=SimpleUnitTest,SimpleTest,CucumberJUnit4Runner
```

## Running the Application

To run the application:

```bash
mvn spring-boot:run
```

## Configuration

Configuration is managed through application properties:

- `application.properties` for main application settings
- `application-test.properties` for test-specific configurations

Key settings include:
- Kafka bootstrap servers, topics, and consumer groups
- CRM API URLs and endpoints
- Logging configurations

## Lessons Learned

1. **Simplicity is Key**: Simpler tests tend to be more reliable and easier to maintain.

2. **Decoupling**: When possible, decouple tests from the full Spring context for faster and more stable tests.

3. **Avoid Duplication**: Code duplication can lead to conflicts and maintenance difficulties.

4. **Proper Configuration**: Understanding how each tool (Cucumber, Spring, JUnit) should be configured, especially when used together.

5. **Test Independence**: Implementing tests that don't depend on external components makes them more robust. 