package com.danieloliveira.order_management.service;

import com.danieloliveira.order_management.exception.ConfiguracaoNaoEncontradaException;
import com.danieloliveira.order_management.model.Configuracao;
import com.danieloliveira.order_management.model.Pedido;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.repository.ConfiguracaoRepository;
import com.danieloliveira.order_management.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final ConfiguracaoRepository configuracaoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public void verificarPrazos() {

        Configuracao configuracaoAtual = configuracaoRepository.findFirstByOrderByIdAsc().orElseThrow(() -> new ConfiguracaoNaoEncontradaException("Configuração não encontrada"));

        LocalTime horarioAlerta = configuracaoAtual.getHorarioLimite().minusMinutes(configuracaoAtual.getMinutosAntecedenciaAlerta());
        LocalTime agora = LocalTime.now();

        boolean dentroDaJanela = !agora.isBefore(horarioAlerta) && agora.isBefore(configuracaoAtual.getHorarioLimite());
        if (!dentroDaJanela)
            return;

        LocalDate hoje = LocalDate.now();
        if (Objects.equals(configuracaoAtual.getUltimaDataAlertada(), hoje))
            return;

        List<Pedido> pedidosPendentes = pedidoRepository.findByDataRetiradaAndStatus(hoje, StatusPedido.PENDENTE);

        if (pedidosPendentes.isEmpty())
            return;

        // mock notificação
        log.info("Você têm {} pedidos pendentes para hoje", pedidosPendentes.size());

        configuracaoAtual.setUltimaDataAlertada(LocalDate.now());
    }
}
