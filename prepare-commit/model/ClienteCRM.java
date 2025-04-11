package com.integration.kafka2crm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Modelo que representa o cliente no formato do CRM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCRM {
    
    @JsonProperty("external_id")
    private String externalId;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("document")
    private String document;
    
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("address")
    private EnderecoCRM address;
    
    @JsonProperty("products")
    private List<ProdutoCRM> products;
    
    @JsonProperty("created_at")
    private LocalDate createdAt;
    
    @JsonProperty("updated_at")
    private LocalDate updatedAt;
} 