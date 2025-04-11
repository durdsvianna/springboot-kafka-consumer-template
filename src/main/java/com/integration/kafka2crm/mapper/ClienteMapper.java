package com.integration.kafka2crm.mapper;

import com.integration.kafka2crm.model.Cliente;
import com.integration.kafka2crm.model.ClienteCRM;
import com.integration.kafka2crm.model.EnderecoCRM;
import com.integration.kafka2crm.model.ProdutoCRM;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper para converter objetos Cliente para ClienteCRM
 */
@Component
public class ClienteMapper {

    /**
     * Converte um objeto Cliente para ClienteCRM
     *
     * @param cliente Objeto de origem
     * @return Objeto ClienteCRM convertido
     */
    public ClienteCRM toClienteCRM(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteCRM clienteCRM = ClienteCRM.builder()
                .externalId(cliente.getId())
                .fullName(cliente.getNome())
                .email(cliente.getEmail())
                .document(cliente.getCpf())
                .birthDate(cliente.getDataNascimento())
                .phone(cliente.getTelefone())
                .createdAt(cliente.getDataCriacao())
                .updatedAt(cliente.getDataAlteracao())
                .build();

        // Converter endereço se disponível
        if (cliente.getEndereco() != null) {
            clienteCRM.setAddress(EnderecoCRM.builder()
                    .street(cliente.getEndereco().getLogradouro())
                    .number(cliente.getEndereco().getNumero())
                    .complement(cliente.getEndereco().getComplemento())
                    .neighborhood(cliente.getEndereco().getBairro())
                    .city(cliente.getEndereco().getCidade())
                    .state(cliente.getEndereco().getEstado())
                    .zipCode(cliente.getEndereco().getCep())
                    .build());
        }

        // Converter produtos se disponíveis
        if (cliente.getProdutos() != null && !cliente.getProdutos().isEmpty()) {
            List<ProdutoCRM> produtosCRM = cliente.getProdutos().stream()
                    .map(produto -> ProdutoCRM.builder()
                            .code(produto.getCodigo())
                            .name(produto.getNome())
                            .description(produto.getDescricao())
                            .price(produto.getValor())
                            .category(produto.getCategoria())
                            .build())
                    .collect(Collectors.toList());
            clienteCRM.setProducts(produtosCRM);
        } else {
            clienteCRM.setProducts(Collections.emptyList());
        }

        return clienteCRM;
    }
} 