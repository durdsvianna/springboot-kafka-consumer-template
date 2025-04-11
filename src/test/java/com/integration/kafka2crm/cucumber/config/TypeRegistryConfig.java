package com.integration.kafka2crm.cucumber.config;

import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.util.Map;

public class TypeRegistryConfig {

    @DataTableType
    public Cliente clienteEntry(Map<String, String> entry) {
        return Cliente.builder()
                .id(entry.get("id"))
                .nome(entry.get("nome"))
                .email(entry.get("email"))
                .telefone(entry.get("telefone"))
                .build();
    }

    @DataTableType
    public ClienteCRM clienteCRMEntry(Map<String, String> entry) {
        return ClienteCRM.builder()
                .externalId(entry.get("id"))
                .fullName(entry.get("name"))
                .email(entry.get("email"))
                .phone(entry.get("phone"))
                .build();
    }
} 