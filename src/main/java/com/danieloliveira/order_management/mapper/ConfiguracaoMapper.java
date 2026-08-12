package com.danieloliveira.order_management.mapper;

import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.model.Configuracao;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ConfiguracaoMapper {

    ConfiguracaoResponseDTO toResponseDTO(Configuracao configuracao);
}
