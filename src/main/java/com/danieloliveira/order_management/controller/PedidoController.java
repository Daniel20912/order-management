package com.danieloliveira.order_management.controller;

import com.danieloliveira.order_management.controller.exception.ErrorMessage;
import com.danieloliveira.order_management.dto.request.PedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.StatusRequestDTO;
import com.danieloliveira.order_management.dto.response.PedidoResponseDTO;
import com.danieloliveira.order_management.dto.response.ResumoDoDiaResponseDTO;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciamento de encomendas: criação, consulta, atualização e acompanhamento de status")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(
            summary = "Criar um novo pedido",
            description = "Cria um pedido com cliente, itens e data de retirada. O valor total e os subtotais " +
                    "são calculados automaticamente pelo backend, e o status inicial é sempre definido como PENDENTE."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = PedidoRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Exemplo de criação de pedido",
                            value = """
                                    {
                                      "cliente": {
                                        "nome": "Maria Silva",
                                        "telefone": "55999998888"
                                      },
                                      "dataRetirada": "2026-07-12",
                                      "observacoes": "Sem cobertura",
                                      "itens": [
                                        { "nomeProduto": "Bolo de chocolate", "quantidade": 2, "valorUnitario": 15.00 },
                                        { "nomeProduto": "Pão", "quantidade": 3, "valorUnitario": 5.00 }
                                      ]
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição (campo obrigatório ausente, valor inválido, etc.)",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO createPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.createPedido(pedidoRequestDTO);
    }

    @Operation(
            summary = "Listar pedidos por data de retirada",
            description = "Retorna todos os pedidos (de qualquer status) cuja data de retirada seja a informada, " +
                    "incluindo os itens completos de cada pedido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos da data informada (pode ser vazia)",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Formato de data inválido",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/date")
    public List<PedidoResponseDTO> findByDate(
            @Parameter(description = "Data de retirada no formato ISO (yyyy-MM-dd)", example = "2026-07-12")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return pedidoService.findPedidosByData(date);
    }

    @Operation(
            summary = "Listar pedidos por status",
            description = "Retorna todos os pedidos (de qualquer data) que estejam com o status informado. " +
                    "Útil, por exemplo, para localizar pedidos PENDENTES esquecidos de dias anteriores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos com o status informado (pode ser vazia)",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Status informado não corresponde a nenhum valor válido",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/status")
    public List<PedidoResponseDTO> findByStatus(
            @Parameter(description = "Status do pedido", example = "PENDENTE")
            @RequestParam("status") StatusPedido status) {
        return pedidoService.findAllByStatus(status);
    }

    @Operation(
            summary = "Buscar pedido por id",
            description = "Retorna os dados completos de um pedido específico, incluindo seus itens."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para o id informado",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/{id}")
    public PedidoResponseDTO findById(
            @Parameter(description = "Id do pedido", example = "1")
            @PathVariable Long id) {
        return pedidoService.findPedidoById(id);
    }

    @Operation(
            summary = "Atualizar um pedido existente",
            description = "Substitui os dados do pedido (cliente, data de retirada, observações e itens) pelos " +
                    "valores informados. A lista de itens é substituída por completo — todos os itens devem ser " +
                    "reenviados, mesmo os que não mudaram. O valor total é recalculado automaticamente. " +
                    "O status do pedido não é alterado por esta operação."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = PedidoRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Exemplo de atualização de pedido",
                            value = """
                                    {
                                      "cliente": {
                                        "nome": "Maria Silva",
                                        "telefone": "55999998888"
                                      },
                                      "dataRetirada": "2026-07-13",
                                      "observacoes": "Cliente pediu para adiar",
                                      "itens": [
                                        { "nomeProduto": "Bolo de chocolate", "quantidade": 3, "valorUnitario": 15.00 },
                                        { "nomeProduto": "Pão", "quantidade": 3, "valorUnitario": 5.00 }
                                      ]
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para o id informado",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PutMapping("/{id}")
    public PedidoResponseDTO updateOrder(
            @Parameter(description = "Id do pedido a ser atualizado", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.updatePedido(id, pedidoRequestDTO);
    }

    @Operation(
            summary = "Atualizar o status de um pedido",
            description = "Altera apenas o status do pedido (PENDENTE, PRONTO, RETIRADO ou CANCELADO), sem afetar " +
                    "os demais campos. As transições entre status são livres, sem restrição de fluxo."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = StatusRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Exemplo de atualização de status",
                            value = """
                                    {
                                      "status": "PRONTO"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Status informado inválido ou ausente",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para o id informado",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping("/{id}/status")
    public PedidoResponseDTO updateStatus(
            @Parameter(description = "Id do pedido", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StatusRequestDTO statusRequestDTO) {
        return pedidoService.updateStatus(id, statusRequestDTO);
    }

    @Operation(
            summary = "Resumo financeiro de um dia",
            description = "Retorna a quantidade de pedidos e o valor total esperado para a data informada, " +
                    "desconsiderando pedidos com status CANCELADO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo calculado com sucesso",
                    content = @Content(schema = @Schema(implementation = ResumoDoDiaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Formato de data inválido",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/summary")
    public ResumoDoDiaResponseDTO dailyResume(
            @Parameter(description = "Data de referência no formato ISO (yyyy-MM-dd)", example = "2026-07-12")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return pedidoService.dailySummary(date);
    }
}
