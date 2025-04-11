package com.integration.kafka2crm.service;

import com.integration.kafka2crm.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to map Cliente objects to ClienteCRM format.
 */
@Slf4j
@Service
public class ClienteMappingService {

    /**
     * Maps a Cliente object to ClienteCRM format.
     *
     * @param cliente The source client object
     * @return The mapped CRM client object
     */
    public ClienteCRM mapToCRM(Cliente cliente) {
        log.debug("Mapping client {} to CRM format", cliente.getId());
        
        // Create the builder with basic information
        ClienteCRM.ClienteCRMBuilder builder = ClienteCRM.builder()
                .externalId(cliente.getId())
                .fullName(cliente.getNome())
                .email(cliente.getEmail())
                .document(cliente.getCpf())
                .birthDate(cliente.getDataNascimento())
                .phone(cliente.getTelefone())
                .createdAt(cliente.getDataCriacao())
                .updatedAt(cliente.getDataAlteracao())
                // Example of the mapped field mentioned in requirements
                .nomeCrm(cliente.getNome());
        
        // Map address if available
        if (cliente.getEndereco() != null) {
            builder.address(mapEndereco(cliente.getEndereco()));
        }
        
        // Map products if available
        if (cliente.getProdutos() != null && !cliente.getProdutos().isEmpty()) {
            builder.products(mapProdutos(cliente.getProdutos()));
        }
        
        return builder.build();
    }
    
    /**
     * Maps an Endereco to EnderecoCRM.
     *
     * @param endereco The source address
     * @return The mapped CRM address
     */
    private EnderecoCRM mapEndereco(Endereco endereco) {
        return EnderecoCRM.builder()
                .street(endereco.getLogradouro())
                .number(endereco.getNumero())
                .complement(endereco.getComplemento())
                .neighborhood(endereco.getBairro())
                .city(endereco.getCidade())
                .state(endereco.getEstado())
                .postalCode(endereco.getCep())
                .build();
    }
    
    /**
     * Maps a list of Produto to a list of ProdutoCRM.
     *
     * @param produtos The source product list
     * @return The mapped CRM product list
     */
    private List<ProdutoCRM> mapProdutos(List<Produto> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            return Collections.emptyList();
        }
        
        return produtos.stream()
                .map(this::mapProduto)
                .collect(Collectors.toList());
    }
    
    /**
     * Maps a Produto to ProdutoCRM.
     *
     * @param produto The source product
     * @return The mapped CRM product
     */
    private ProdutoCRM mapProduto(Produto produto) {
        return ProdutoCRM.builder()
                .code(produto.getCodigo())
                .name(produto.getNome())
                .description(produto.getDescricao())
                .price(produto.getPreco())
                .quantity(produto.getQuantidade())
                .build();
    }
} 