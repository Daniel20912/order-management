package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 150, message = "O telefone deve ter no máximo 150 caracteres")
    private String nomeProduto;

    @NotNull(message = "A quantida de produtos deve ser definida")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "O valor unitário deve ser definido")
    @DecimalMin(value = "0.01", message = "O valor unitário deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O valor deve possuir no máximo 2 casas decimais")
    private BigDecimal valorUnitario;
}
