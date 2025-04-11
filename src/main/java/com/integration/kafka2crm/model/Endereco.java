package com.integration.kafka2crm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing an address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    
} 