package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nome;

    private String telefone;
}
