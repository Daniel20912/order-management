package com.danieloliveira.order_management.controller;

import com.danieloliveira.order_management.dto.request.PedidoRequestDTO;
import com.danieloliveira.order_management.dto.request.StatusRequestDTO;
import com.danieloliveira.order_management.dto.response.PedidoResponseDTO;
import com.danieloliveira.order_management.dto.response.ResumoDoDiaResponseDTO;
import com.danieloliveira.order_management.model.StatusPedido;
import com.danieloliveira.order_management.service.PedidoService;
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
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO createPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.createPedido(pedidoRequestDTO);
    }

    @GetMapping("/date")
    public List<PedidoResponseDTO> findByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return pedidoService.findPedidosByData(date);
    }

    @GetMapping("/status")
    public List<PedidoResponseDTO> findByStatus(@RequestParam("status") StatusPedido status) {
        return pedidoService.findAllByStatus(status);
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO findById(@PathVariable Long id) throws Exception {
        return pedidoService.findPedidoById(id);
    }

    @PutMapping("/{id}")
    public PedidoResponseDTO updateOrder(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) throws Exception {
        return pedidoService.updatePedido(id, pedidoRequestDTO);
    }

    @PatchMapping("/{id}/status")
    public PedidoResponseDTO updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequestDTO statusRequestDTO) throws Exception {
        return pedidoService.updateStatus(id, statusRequestDTO);
    }

    @GetMapping("/summary")
    public ResumoDoDiaResponseDTO dailyResume(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return pedidoService.dailySummary(date);
    }
}
