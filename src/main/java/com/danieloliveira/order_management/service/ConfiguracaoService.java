package com.danieloliveira.order_management.service;

import com.danieloliveira.order_management.dto.request.ConfiguracaoRequestDTO;
import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.mapper.ConfiguracaoMapper;
import com.danieloliveira.order_management.model.Configuracao;
import com.danieloliveira.order_management.repository.ConfiguracaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;
    private final ConfiguracaoMapper configuracaoMapper;

    public ConfiguracaoResponseDTO findFirstByOrderByIdAsc() throws Exception {
        Configuracao configuracao = getConfiguracao();
        return configuracaoMapper.toResponseDTO(configuracao);
    }

    private @NonNull Configuracao getConfiguracao() throws Exception {
        return configuracaoRepository.findFirstByOrderByIdAsc().orElseThrow(Exception::new);
    }

    @Transactional
    public ConfiguracaoResponseDTO update(ConfiguracaoRequestDTO configuracaoRequestDTO) throws Exception {
        Configuracao configuracao = getConfiguracao();

        configuracao.setHorarioLimite(configuracaoRequestDTO.getHorarioLimite());
        configuracao.setMinutosAntecedenciaAlerta(configuracaoRequestDTO.getMinutosAntecedenciaAlerta());

        return configuracaoMapper.toResponseDTO(configuracao);
    }
}
