package com.danieloliveira.order_management.repository;

import com.danieloliveira.order_management.model.Pedido;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.repository.projection.ResumoDiarioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.dataRetirada = :data")
    List<Pedido> buscarPorDataComItens(@Param("data") LocalDate data);

    @Query("SELECT COUNT(p) AS quantidade, COALESCE(SUM(p.valorTotal), 0) AS valorTotal " +
            "FROM Pedido p WHERE p.dataRetirada = :data AND p.status <> 'CANCELADO'")
    ResumoDiarioProjection buscarResumoDoDia(@Param("data") LocalDate data);

    List<Pedido> findByDataRetiradaAndStatus(LocalDate data, StatusPedido status);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.status = :status ORDER BY p.dataRetirada ASC")
    List<Pedido> buscarPorStatusComItensOrdenadoPorData(@Param("status") StatusPedido status);
}
