package com.danieloliveira.order_management.service;

import com.danieloliveira.order_management.dto.request.PedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.StatusRequestDTO;
import com.danieloliveira.order_management.dto.response.PedidoResponseDTO;
import com.danieloliveira.order_management.mapper.PedidoMapper;
import com.danieloliveira.order_management.model.ItemPedido;
import com.danieloliveira.order_management.model.Pedido;
import com.danieloliveira.order_management.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // METODOS AUXILIARES
    private static void calcValorTotal(Pedido pedidoEntity) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : pedidoEntity.getItens())
            total = total.add(item.getSubtotal());

        pedidoEntity.setValorTotal(total);
    }

    private static void calcSubtotalDosItens(Pedido pedidoEntity) {
        pedidoEntity.getItens().forEach(item ->
                item.setSubtotal(item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
        );
    }

    private @NonNull Pedido findEntityById(Long pedidoId) throws Exception {
        return pedidoRepository.findById(pedidoId).orElseThrow(Exception::new);
    }

    private void updateEntity(Pedido pedido, PedidoRequestDTO pedidoRequestDTO) {

        pedido.setDadosCliente(pedidoMapper.toEntity(pedidoRequestDTO.getCliente()));
        pedido.setDataRetirada(pedidoRequestDTO.getDataRetirada());
        pedido.setObservacoes(pedidoRequestDTO.getObservacoes());
        pedido.getItens().clear();
        pedidoRequestDTO.getItens().forEach(itemDTO -> {

            ItemPedido item = pedidoMapper.toEntity(itemDTO);
            item.setPedido(pedido);
            pedido.getItens().add(item);
        });

        // recalcula os valores
        calcSubtotalDosItens(pedido);
        calcValorTotal(pedido);
    }

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PedidoResponseDTO createPedido(PedidoRequestDTO pedidoRequestDTO) {

        // converte o dto para entidade e vincula cada ItemPedido de volta ao Pedido
        Pedido pedidoEntity = pedidoMapper.toEntity(pedidoRequestDTO);
        pedidoEntity.getItens().forEach(item -> item.setPedido(pedidoEntity));

        // calcula o subtotal de cada item
        calcSubtotalDosItens(pedidoEntity);

        // calcula o total do pedido
        calcValorTotal(pedidoEntity);

        // salva o pedido no banco de dados
        pedidoRepository.save(pedidoEntity);

        // converte o pedido para um dto de resposta e retorna
        return pedidoMapper.toResponseDTO(pedidoEntity);
    }

    public List<PedidoResponseDTO> findPedidosByData(LocalDate date) {
        List<PedidoResponseDTO> pedidosResponseDTO = new ArrayList<>();

        pedidoRepository.buscarPorDataComItens(date)
                .forEach(pedido -> {
                    pedidosResponseDTO.add(pedidoMapper.toResponseDTO(pedido));
                });

        return pedidosResponseDTO;
    }

    public PedidoResponseDTO findPedidoById(Long id) throws Exception {
        Pedido pedido = findEntityById(id);
        return pedidoMapper.toResponseDTO(pedido);
    }

    // atualiza o status usando dirty checking
    @Transactional
    public PedidoResponseDTO updateStatus(Long pedidoId, StatusRequestDTO statusRequestDTO) throws Exception {
        Pedido pedido = findEntityById(pedidoId);

        pedido.setStatus(statusRequestDTO.getStatusPedido());

        return pedidoMapper.toResponseDTO(pedido);
    }

    // atualiza o pedido usando dirty checking
    @Transactional
    public PedidoResponseDTO updatePedido(Long pedidoId, PedidoRequestDTO pedidoRequestDTO) throws Exception {
        Pedido pedido = findEntityById(pedidoId);

        updateEntity(pedido, pedidoRequestDTO);
        return pedidoMapper.toResponseDTO(pedido);
    }

}
