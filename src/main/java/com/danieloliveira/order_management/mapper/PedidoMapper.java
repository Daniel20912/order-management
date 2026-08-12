package com.danieloliveira.order_management.mapper;

import com.danieloliveira.order_management.dto.request.ClienteRequestDTO;
import com.danieloliveira.order_management.dto.request.ItemPedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.PedidoRequestDTO;
import com.danieloliveira.order_management.dto.response.ClienteResponseDTO;
import com.danieloliveira.order_management.dto.response.ItemPedidoResponseDTO;
import com.danieloliveira.order_management.dto.response.PedidoResponseDTO;
import com.danieloliveira.order_management.model.DadosCliente;
import com.danieloliveira.order_management.model.ItemPedido;
import com.danieloliveira.order_management.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Pedido toEntity(PedidoRequestDTO pedidoRequestDTO);

    PedidoResponseDTO toResponseDTO(Pedido pedido);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    ItemPedido toEntity(ItemPedidoRequestDTO itemPedidoRequestDTO);

    ItemPedidoResponseDTO toResponseDTO(ItemPedido itemPedido);

    DadosCliente toEntity(ClienteRequestDTO clienteRequestDTO);

    ClienteResponseDTO toResponseDTO(DadosCliente dadosCliente);
}
