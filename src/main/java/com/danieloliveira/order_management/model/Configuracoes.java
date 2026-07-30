package com.danieloliveira.order_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "configuracoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Configuracoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    private Integer minutosAntecedenciaAlerta;
}
