package com.danieloliveira.order_management.dto.response;

import com.danieloliveira.order_management.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long pedidoId;

    private ClienteResponseDTO cliente;

    private LocalDate dataRetirada;

    private String observacoes;

    private StatusPedido status;

    private BigDecimal valorTotal;

    private List<ItemPedidoResponseDTO> itens;

    private LocalDate dataCriacao;

    private LocalDate dataAtualizacao;
}
