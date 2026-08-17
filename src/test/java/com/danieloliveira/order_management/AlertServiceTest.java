package com.danieloliveira.order_management;

import com.danieloliveira.order_management.model.Configuracao;
import com.danieloliveira.order_management.model.Pedido;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.repository.ConfiguracaoRepository;
import com.danieloliveira.order_management.repository.PedidoRepository;
import com.danieloliveira.order_management.service.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private ConfiguracaoRepository configuracaoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @InjectMocks
    private AlertService alertService;

    @Test
    void deveAlertarQuandoDentroDaJanelaEExistemPendentes() {
        Configuracao config = new Configuracao();
        config.setHorarioLimite(LocalTime.now().plusMinutes(10));
        config.setMinutosAntecedenciaAlerta(30);
        config.setUltimaDataAlertada(null);

        when(configuracaoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(config));
        when(pedidoRepository.findByDataRetiradaAndStatus(any(), eq(StatusPedido.PENDENTE)))
                .thenReturn(List.of(new Pedido()));

        alertService.verificarPrazos();

        // Valida se o atributo da entidade foi atualizado corretamente pelo serviço
        assertNotNull(config.getUltimaDataAlertada());
        assertEquals(LocalDate.now(), config.getUltimaDataAlertada());
    }
}
