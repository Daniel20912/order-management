package com.danieloliveira.order_management.controller.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorMessage {

    // corpo da mensagem de erro
    private final String path; // mostra a URL acessada (ex: /api/pedidos/99)
    private final String method; // mostra o verbo HTTP usado (GET, POST, etc.)
    private final int status; // mostra o código de erro (404, 400, etc.)
    private final String statusText; // texto do erro do protocolo HTTP (NOT FOUND, BAD REQUEST)
    private final String message; // mensagem customizada criada para a exceção
    @JsonInclude(JsonInclude.Include.NON_NULL) // esse atributo só vai incluído na mensagem se ele não for nulo
    private Map<String, String> errors; // guarda os erros de validação de campos


    // construtor para erros mais gerais
    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
    }

    // contrutor para erros de validação de campos
    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult bindingResult) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
        addErrors(bindingResult);
    }

    private void addErrors(BindingResult bindingResult) {
        this.errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            // o .getField() é o nome do campo onde aconteceu o erro de validação
            // o .getDefaultMessage() é a mensagem que diz por que a validação falhou
            this.errors.put(error.getField(), error.getDefaultMessage());
        }
    }
}
