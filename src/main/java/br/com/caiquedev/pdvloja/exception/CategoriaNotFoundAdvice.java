package br.com.caiquedev.pdvloja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoriaNotFoundAdvice {

    @ExceptionHandler(CategoriaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String categoriaNotFoundHandler(CategoriaNotFoundException ex) {
        return ex.getMessage();
    }
}
