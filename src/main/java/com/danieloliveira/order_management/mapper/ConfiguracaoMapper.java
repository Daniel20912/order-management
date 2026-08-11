package com.danieloliveira.order_management.mapper;

import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.model.Configuracao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfiguracaoMapper {

    ConfiguracaoResponseDTO toResponseDTO(Configuracao configuracao);
}
