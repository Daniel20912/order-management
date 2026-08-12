package com.danieloliveira.order_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracaoRequestDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @NotNull(message = "O horário limite deve ser informado")
    private LocalTime horarioLimite;

    @NotNull(message = "O minutos de antecedência para o alerta devem ser informados")
    @Min(value = 0, message = "Os minutos de antecedência não podem ser negativos")
    private Integer minutosAntecedenciaAlerta;
}
