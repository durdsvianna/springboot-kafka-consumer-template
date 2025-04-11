package com.integration.kafka2crm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Model representing a product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    
} 