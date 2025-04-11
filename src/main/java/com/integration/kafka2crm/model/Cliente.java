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
 * Model that represents a client received from Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    private String id;
    
    private String nome;
    
    @JsonProperty("data_criacao")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCriacao;
    
    @JsonProperty("data_alteracao")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAlteracao;
    
    private String email;
    
    private String cpf;
    
    private LocalDate dataNascimento;
    
    private String telefone;
    
    private Endereco endereco;
    
    private List<Produto> produtos;
} 