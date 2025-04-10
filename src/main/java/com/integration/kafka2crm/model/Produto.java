package com.integration.kafka2crm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo que representa um produto associado a um cliente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    
    private String codigo;
    
    private String nome;
    
    private String descricao;
    
    private BigDecimal valor;
    
    private String categoria;
} 