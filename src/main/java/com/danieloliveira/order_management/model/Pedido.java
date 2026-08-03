package com.danieloliveira.order_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nome", column = @Column(name = "cliente_nome", nullable = false)),
            @AttributeOverride(name = "telefone", column = @Column(name = "cliente_telefone"))
    })
    private DadosCliente dadosCliente;

    @Column(nullable = false)
    private LocalDate dataRetirada;

    private String observacoes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;


    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
