package com.danieloliveira.order_management.service;

import com.danieloliveira.order_management.dto.request.ConfiguracaoRequestDTO;
import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.exception.ConfiguracaoNaoEncontradaException;
import com.danieloliveira.order_management.mapper.ConfiguracaoMapper;
import com.danieloliveira.order_management.model.Configuracao;
import com.danieloliveira.order_management.repository.ConfiguracaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;
    private final ConfiguracaoMapper configuracaoMapper;

    private Configuracao getConfiguracao() {
        return configuracaoRepository.findFirstByOrderByIdAsc().orElseThrow(() -> new ConfiguracaoNaoEncontradaException("Configuração não encontrada"));
    }

    public ConfiguracaoResponseDTO findFirstByOrderByIdAsc() {
        Configuracao configuracao = getConfiguracao();
        return configuracaoMapper.toResponseDTO(configuracao);
    }

    @Transactional
    public ConfiguracaoResponseDTO update(ConfiguracaoRequestDTO configuracaoRequestDTO) {
        Configuracao configuracao = getConfiguracao();

        configuracao.setHorarioLimite(configuracaoRequestDTO.getHorarioLimite());
        configuracao.setMinutosAntecedenciaAlerta(configuracaoRequestDTO.getMinutosAntecedenciaAlerta());

        return configuracaoMapper.toResponseDTO(configuracao);
    }
}
