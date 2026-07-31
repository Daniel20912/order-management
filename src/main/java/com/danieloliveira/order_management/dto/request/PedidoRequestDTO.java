package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "Os dados do cliente são obrigatórios")
    private ClienteRequest cliente;

    @NotBlank(message = "A data de retirada do pedido é obrigatória")
    private LocalDate dataRetirada;

    private String observacoes;

    private List<ItemPedidoRequestDTO> itens;
}
