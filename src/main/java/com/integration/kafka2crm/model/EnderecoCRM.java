package com.integration.kafka2crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo que representa o endereço no formato do CRM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoCRM {
    
    @JsonProperty("street")
    private String street;
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("complement")
    private String complement;
    
    @JsonProperty("neighborhood")
    private String neighborhood;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("zip_code")
    private String zipCode;
} 