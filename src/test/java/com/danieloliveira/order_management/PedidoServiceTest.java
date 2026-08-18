package com.danieloliveira.order_management;

import com.danieloliveira.order_management.dto.request.ClienteRequestDTO;
import com.danieloliveira.order_management.dto.request.ItemPedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.PedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.StatusRequestDTO;
import com.danieloliveira.order_management.dto.response.PedidoResponseDTO;
import com.danieloliveira.order_management.exception.PedidoNaoEncontradoException;
import com.danieloliveira.order_management.mapper.PedidoMapper;
import com.danieloliveira.order_management.model.DadosCliente;
import com.danieloliveira.order_management.model.ItemPedido;
import com.danieloliveira.order_management.model.Pedido;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.repository.PedidoRepository;
import com.danieloliveira.order_management.service.PedidoService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private static @NonNull Pedido createPedidoMapeado(PedidoRequestDTO requestDTO) {
        Pedido pedidoMapeado = new Pedido();
        pedidoMapeado.setDataRetirada(requestDTO.getDataRetirada());

        ItemPedido item1 = new ItemPedido();
        item1.setNomeProduto("Bolo de chocolate");
        item1.setQuantidade(2);
        item1.setValorUnitario(new BigDecimal("15.00"));

        ItemPedido item2 = new ItemPedido();
        item2.setNomeProduto("Pão");
        item2.setQuantidade(3);
        item2.setValorUnitario(new BigDecimal("5.00"));

        pedidoMapeado.setItens(new ArrayList<>(List.of(item1, item2)));
        return pedidoMapeado;
    }

    private PedidoRequestDTO criarPedidoRequestValido() {
        ClienteRequestDTO cliente = new ClienteRequestDTO();
        cliente.setNome("Maria");
        cliente.setTelefone("99999-9999");

        ItemPedidoRequestDTO item1 = new ItemPedidoRequestDTO();
        item1.setNomeProduto("Bolo de chocolate");
        item1.setQuantidade(2);
        item1.setValorUnitario(new BigDecimal("15.00"));

        ItemPedidoRequestDTO item2 = new ItemPedidoRequestDTO();
        item2.setNomeProduto("Pão");
        item2.setQuantidade(3);
        item2.setValorUnitario(new BigDecimal("5.00"));

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setCliente(cliente);
        dto.setDataRetirada(LocalDate.of(2026, 7, 12));
        dto.setObservacoes("Sem cobertura");
        dto.setItens(List.of(item1, item2));
        return dto;
    }

    @Test
    void deveCalcularSubtotalEValorTotalAoCriarPedido() {
        PedidoRequestDTO requestDTO = criarPedidoRequestValido();

        // Simula o que o mapper faria: monta a entidade a partir do DTO, sem valores calculados ainda
        Pedido pedidoMapeado = createPedidoMapeado(requestDTO);

        when(pedidoMapper.toEntity(requestDTO)).thenReturn(pedidoMapeado);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        pedidoService.createPedido(requestDTO);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        Pedido pedidoSalvo = captor.getValue();

        // Verifica os subtotais de cada item
        assertEquals(new BigDecimal("30.00"), pedidoSalvo.getItens().get(0).getSubtotal());
        assertEquals(new BigDecimal("15.00"), pedidoSalvo.getItens().get(1).getSubtotal());

        // Verifica o valor total do pedido (soma dos subtotais)
        assertEquals(new BigDecimal("45.00"), pedidoSalvo.getValorTotal());
    }

    @Test
    void deveVincularCadaItemAoPedidoAoCriar() {
        PedidoRequestDTO requestDTO = criarPedidoRequestValido();

        Pedido pedidoMapeado = new Pedido();
        ItemPedido item = new ItemPedido();
        item.setQuantidade(1);
        item.setValorUnitario(BigDecimal.TEN);
        pedidoMapeado.setItens(new ArrayList<>(List.of(item)));

        when(pedidoMapper.toEntity(requestDTO)).thenReturn(pedidoMapeado);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        pedidoService.createPedido(requestDTO);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        Pedido pedidoSalvo = captor.getValue();

        pedidoSalvo.getItens().forEach(i ->
                assertEquals(pedidoSalvo, i.getPedido())
        );
    }

    @Test
    void devecriarPedidoComStatusPendenteMesmoQueEntidadeVenhaSemStatusDefinido() {
        PedidoRequestDTO requestDTO = criarPedidoRequestValido();
        Pedido pedidoMapeado = new Pedido();
        pedidoMapeado.setItens(new ArrayList<>());

        when(pedidoMapper.toEntity(requestDTO)).thenReturn(pedidoMapeado);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        pedidoService.createPedido(requestDTO);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        assertEquals(StatusPedido.PENDENTE, captor.getValue().getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        Long idInexistente = 999L;
        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(PedidoNaoEncontradoException.class, () -> pedidoService.findPedidoById(idInexistente));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveAtualizarStatusSemAlterarOutrosCampos() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setStatus(StatusPedido.PENDENTE);
        pedidoExistente.setValorTotal(new BigDecimal("50.00"));

        StatusRequestDTO statusDTO = new StatusRequestDTO();
        statusDTO.setStatusPedido(StatusPedido.PRONTO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        // Act
        pedidoService.updateStatus(1L, statusDTO);

        // Assert (Verificação)
        assertEquals(StatusPedido.PRONTO, pedidoExistente.getStatus());
        assertEquals(new BigDecimal("50.00"), pedidoExistente.getValorTotal()); // não deveria mudar
    }

    @Test
    void deveRecalcularValorTotalAoEditarItens() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setStatus(StatusPedido.PENDENTE);

        ItemPedido itemAntigo = new ItemPedido();
        itemAntigo.setSubtotal(new BigDecimal("30.00"));
        pedidoExistente.setItens(new ArrayList<>(List.of(itemAntigo)));
        pedidoExistente.setValorTotal(new BigDecimal("30.00"));

        ItemPedidoRequestDTO novoItemDTO = new ItemPedidoRequestDTO();
        novoItemDTO.setNomeProduto("Donut");
        novoItemDTO.setQuantidade(4);
        novoItemDTO.setValorUnitario(new BigDecimal("8.00"));

        PedidoRequestDTO editarDTO = criarPedidoRequestValido();
        editarDTO.setItens(List.of(novoItemDTO));

        ItemPedido novoItemMapeado = new ItemPedido();
        novoItemMapeado.setNomeProduto("Donut");
        novoItemMapeado.setQuantidade(4);
        novoItemMapeado.setValorUnitario(new BigDecimal("8.00"));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoMapper.toEntity(any(ClienteRequestDTO.class))).thenReturn(mock(DadosCliente.class));
        when(pedidoMapper.toEntity(novoItemDTO)).thenReturn(novoItemMapeado);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        // Act
        pedidoService.updatePedido(1L, editarDTO);

        // Assert
        verify(pedidoRepository).flush();

        assertEquals(1, pedidoExistente.getItens().size());
        assertEquals(new BigDecimal("32.00"), pedidoExistente.getValorTotal());
        assertEquals(StatusPedido.PENDENTE, pedidoExistente.getStatus());
    }
}
