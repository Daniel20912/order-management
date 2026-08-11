package com.danieloliveira.order_management.controller;

import com.danieloliveira.order_management.dto.request.ConfiguracaoRequestDTO;
import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.service.ConfiguracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    @GetMapping
    public ConfiguracaoResponseDTO getConfiguracao() throws Exception {
        return configuracaoService.findFirstByOrderByIdAsc();
    }

    @PutMapping
    public ConfiguracaoResponseDTO updateConfiguracao(ConfiguracaoRequestDTO configuracaoRequestDTO) throws Exception {
        return configuracaoService.update(configuracaoRequestDTO);
    }
}
