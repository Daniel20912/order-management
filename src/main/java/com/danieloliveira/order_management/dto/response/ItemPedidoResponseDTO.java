package com.danieloliveira.order_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ItemPedidoResponseDTO {

    private String nomeProduto;

    private Integer quantidade;

    private BigDecimal valorUnitario;
}
