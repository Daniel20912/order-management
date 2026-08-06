package com.danieloliveira.order_management.repository;

import com.danieloliveira.order_management.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {

    Optional<Configuracao> findFirstByOrderByIdAsc();
}
