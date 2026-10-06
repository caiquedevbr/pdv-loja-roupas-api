package br.com.caiquedev.pdvloja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EstoqueNotFoundAdvice {

    @ExceptionHandler(EstoqueNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String estoqueNotFoundHandler(EstoqueNotFoundException ex) {
        return ex.getMessage();
    }
}
