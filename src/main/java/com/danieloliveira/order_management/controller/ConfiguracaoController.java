package com.danieloliveira.order_management.controller;

import com.danieloliveira.order_management.controller.exception.ErrorMessage;
import com.danieloliveira.order_management.dto.request.ConfiguracaoRequestDTO;
import com.danieloliveira.order_management.dto.response.ConfiguracaoResponseDTO;
import com.danieloliveira.order_management.service.ConfiguracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
@Tag(name = "Configuração", description = "Configuração global de horário limite de produção e antecedência de alertas")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    @Operation(
            summary = "Buscar a configuração atual",
            description = "Retorna o horário limite de produção e a antecedência (em minutos) configurada para o alerta de prazo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConfiguracaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma configuração encontrada (estado inconsistente do sistema)",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping
    public ConfiguracaoResponseDTO getConfiguracao() {
        return configuracaoService.findFirstByOrderByIdAsc();
    }

    @Operation(
            summary = "Atualizar a configuração",
            description = "Atualiza o horário limite de produção e/ou a antecedência do alerta. " +
                    "Como o sistema possui uma única configuração global, não é necessário informar um id."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = ConfiguracaoRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Exemplo de atualização de configuração",
                            value = """
                                    {
                                      "horarioLimite": "18:00",
                                      "minutosAntecedenciaAlerta": 60
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConfiguracaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma configuração encontrada (estado inconsistente do sistema)",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PutMapping
    public ConfiguracaoResponseDTO updateConfiguracao(@Valid @RequestBody ConfiguracaoRequestDTO configuracaoRequestDTO) {
        return configuracaoService.update(configuracaoRequestDTO);
    }
}
