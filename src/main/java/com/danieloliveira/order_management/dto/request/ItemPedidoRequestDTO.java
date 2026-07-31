package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nomeProduto;

    @NotNull(message = "A quantida de produtos deve ser definida")
    private Integer quantidade;

    @NotNull(message = "O valor unitário deve ser definido")
    private BigDecimal valorUnitario;
}
