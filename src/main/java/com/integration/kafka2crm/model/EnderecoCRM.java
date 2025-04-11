package com.integration.kafka2crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing an address in CRM format.
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
    
    @JsonProperty("postal_code")
    private String postalCode;
    
} 