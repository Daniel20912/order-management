package com.danieloliveira.order_management.controller.exception;

import com.danieloliveira.order_management.exception.ConfiguracaoNaoEncontradaException;
import com.danieloliveira.order_management.exception.PedidoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> pedidoNaoEncontrado(PedidoNaoEncontradoException ex, HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ConfiguracaoNaoEncontradaException.class)
    public ResponseEntity<ErrorMessage> configuracaoNaoEncontrada(ConfiguracaoNaoEncontradaException ex, HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValid(HttpServletRequest request, BindingResult bindingResult) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Campo(s) Inválido(s)", bindingResult));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format(
                "O valor '%s' informado para o parâmetro '%s' é inválido.",
                ex.getValue(), ex.getName()
        );

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] valoresValidos = ex.getRequiredType().getEnumConstants();
            message += " Valores aceitos: " + Arrays.toString(valoresValidos);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> httpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.error("JSON inválido recebido em {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "O corpo da requisição está ausente ou não é um JSON válido.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> exception(Exception ex, HttpServletRequest request) {

        log.error("Erro não tratado", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado"));
    }
}
