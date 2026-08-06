package com.danieloliveira.order_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ResumoDoDiaResponseDTO {

    private LocalDate data;

    private Long quantidadePedidos;

    private BigDecimal valorTotalEsperado;
}
