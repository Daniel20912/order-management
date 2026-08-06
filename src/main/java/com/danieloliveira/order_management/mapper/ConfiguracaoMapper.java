package com.danieloliveira.order_management.mapper;

import com.danieloliveira.order_management.dto.request.ConfiguracaoRequestDTO;
import com.danieloliveira.order_management.model.Configuracao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfiguracaoMapper {

    Configuracao toEntity(ConfiguracaoRequestDTO configuracaoRequestDTO);

    ConfiguracaoRequestDTO toResponseDTO(Configuracao configuracao);
}
