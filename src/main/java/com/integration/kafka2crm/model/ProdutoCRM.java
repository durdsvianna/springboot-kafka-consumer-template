package com.integration.kafka2crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Model representing a product in CRM format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoCRM {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
} 