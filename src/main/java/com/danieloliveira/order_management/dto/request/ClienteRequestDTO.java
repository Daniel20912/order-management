package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    private String telefone;
}
