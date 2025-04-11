package com.integration.kafka2crm.service;

import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.model.EnderecoCRM;
import com.integration.kafka2crm.model.ProdutoCRM;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Componente responsável por mapear um Cliente para o formato do CRM.
 */
@Component
public class ClienteMapper {

    /**
     * Converte um objeto Cliente para um objeto ClienteCRM.
     * 
     * @param cliente O objeto Cliente a ser convertido
     * @return Um objeto ClienteCRM com os dados mapeados
     */
    public ClienteCRM mapToCRM(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        return ClienteCRM.builder()
                .externalId(cliente.getId())
                .fullName(cliente.getNome())
                .email(cliente.getEmail())
                .document(cliente.getCpf())
                .birthDate(cliente.getDataNascimento())
                .phone(cliente.getTelefone())
                .address(mapEndereco(cliente))
                .products(mapProdutos(cliente))
                .createdAt(cliente.getDataCriacao())
                .updatedAt(cliente.getDataAlteracao())
                .build();
    }
    
    /**
     * Mapeia o endereço do cliente para o formato do CRM.
     * 
     * @param cliente O objeto Cliente que contém o endereço
     * @return Um objeto EnderecoCRM com os dados mapeados
     */
    private EnderecoCRM mapEndereco(Cliente cliente) {
        if (cliente.getEndereco() == null) {
            return null;
        }
        
        return EnderecoCRM.builder()
                .street(cliente.getEndereco().getLogradouro())
                .number(cliente.getEndereco().getNumero())
                .complement(cliente.getEndereco().getComplemento())
                .neighborhood(cliente.getEndereco().getBairro())
                .city(cliente.getEndereco().getCidade())
                .state(cliente.getEndereco().getEstado())
                .zipCode(cliente.getEndereco().getCep())
                .build();
    }
    
    /**
     * Mapeia os produtos do cliente para o formato do CRM.
     * 
     * @param cliente O objeto Cliente que contém os produtos
     * @return Uma lista de objetos ProdutoCRM com os dados mapeados
     */
    private java.util.List<ProdutoCRM> mapProdutos(Cliente cliente) {
        if (cliente.getProdutos() == null) {
            return java.util.Collections.emptyList();
        }
        
        return cliente.getProdutos().stream()
                .map(produto -> ProdutoCRM.builder()
                        .code(produto.getCodigo())
                        .name(produto.getNome())
                        .description(produto.getDescricao())
                        .price(produto.getValor())
                        .category(produto.getCategoria())
                        .build())
                .collect(Collectors.toList());
    }
} 