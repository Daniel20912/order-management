package com.danieloliveira.order_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracaoRequestDTO {

    @NotNull(message = "O horário limite deve ser informado")
    private LocalTime horarioLimite;

    @NotNull(message = "O minutos de antecedência para o alerta devem ser informados")
    private Integer minutosAntecedenciaAlerta;
}
