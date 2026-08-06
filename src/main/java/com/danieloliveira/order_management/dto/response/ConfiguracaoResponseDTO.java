package com.danieloliveira.order_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ConfiguracaoResponseDTO {

    private LocalTime horarioLimite;

    private Integer minutosAntecedenciaAlerta;
}
