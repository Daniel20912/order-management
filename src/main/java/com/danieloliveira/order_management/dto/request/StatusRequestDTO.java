package com.danieloliveira.order_management.dto.request;

import com.danieloliveira.order_management.model.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusRequestDTO {

    @NotNull(message = "O status do pedido precisa ser informado")
    private StatusPedido statusPedido;
}
